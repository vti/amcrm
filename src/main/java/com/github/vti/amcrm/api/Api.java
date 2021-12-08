package com.github.vti.amcrm.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.file.FileService;
import com.linecorp.armeria.server.logging.AccessLogWriter;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.api.service.*;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.command.CreateUserCommand;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.infra.photo.LocalPhotoStorage;
import com.github.vti.amcrm.infra.photo.PhotoStorage;
import com.github.vti.amcrm.infra.registry.RegistryFactory;
import com.github.vti.amcrm.infra.registry.ViewRegistry;

class Args {
    @Parameter(names = "--config", description = "Path to config file")
    public String configFile = null;

    @Parameter(names = "--port", description = "Server port")
    public Integer port = null;

    @Parameter(names = "--debug", description = "Debug level")
    public Boolean debug = null;
}

public final class Api {
    private static final Logger log = LogManager.getLogger(Api.class);
    private static final String ACCESS_LOG_FORMAT =
            "%a %l %u [%t] \"%r\" %>s %b \"%{User-agent}i\"";

    private final Config config;
    private Server server;

    private final RegistryFactory registryFactory;
    private final Path publicDir;
    private final PhotoStorage photoStorage;

    public Api(Config config) {
        this.config = config;

        log.info("Loaded configuration: {}", config.toString());

        registryFactory = new RegistryFactory(config.getStorage(), config.getBaseUrl().toString());
        publicDir = Paths.get("public");
        photoStorage =
                new LocalPhotoStorage(Paths.get(publicDir.toString()), Paths.get("customer"));
    }

    public RegistryFactory getRegistryFactory() {
        return registryFactory;
    }

    public Optional<SessionId> makeSureAtLeastAdminExists() {
        UserRepository userRepository = registryFactory.getRepositoryRegistry().getUserRepository();

        if (userRepository.isEmpty()) {
            UserId userId = UserId.of(UUID.randomUUID().toString());

            CreateUserCommand command =
                    CreateUserCommand.builder()
                            .userRepository(userRepository)
                            .actorId(ActorId.of(UUID.randomUUID().toString()))
                            .id(userId)
                            .name("admin")
                            .admin(true)
                            .build();

            try {
                command.execute();
            } catch (UserExistsException e) {
                throw new RuntimeException("Failed to create the first admin", e);
            }

            log.info("Created first admin: {}", userId);

            Session session =
                    Session.builder()
                            .id(SessionId.of(UUID.randomUUID().toString()))
                            .actorId(ActorId.of(userId.value()))
                            .expiresAt(Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(1)))
                            .build();
            registryFactory.getRepositoryRegistry().getSessionRepository().store(session);

            log.info("Created admin session: {}", session);

            return Optional.of(session.getId());
        }

        return Optional.empty();
    }

    public void start() {
        server =
                newServer(
                        config.getPort(),
                        registryFactory.getRepositoryRegistry(),
                        registryFactory.getViewRegistry(),
                        photoStorage,
                        publicDir);

        server.start().join();

        log.info(
                "Server started. Serving DocService at http://127.0.0.1:{}/docs",
                server.activeLocalPort());
    }

    public void stop() {
        server.stop().join();
    }

    private Server newServer(
            int httpPort,
            RepositoryRegistry repositoryRegistry,
            ViewRegistry viewRegistry,
            PhotoStorage photoStorage,
            Path publicDir) {
        return Server.builder()
                .http(httpPort)
                .decoratorUnder(
                        "/",
                        (delegate, ctx, req) ->
                                new AuthenticationService(delegate, registryFactory)
                                        .serve(ctx, req))
                .accessLogWriter(AccessLogWriter.custom(ACCESS_LOG_FORMAT), true)
                .annotatedService(
                        Resource.OAUTH.value(),
                        new OauthService(registryFactory, config.getOauth()))
                .annotatedService(Resource.PING.value(), new PingService())
                .annotatedService(
                        Resource.CUSTOMERS.value(),
                        new CustomerService(repositoryRegistry, viewRegistry, photoStorage))
                .annotatedService(
                        Resource.USERS.value(), new UserService(repositoryRegistry, viewRegistry))
                .serviceUnder("/docs", DocService.builder().build())
                .serviceUnder("/", FileService.of(publicDir))
                .build();
    }

    private static Config configure(Args args) {
        Config.Builder configBuilder = Config.builder();

        if (args.configFile != null) {
            try {
                configBuilder.load(new File(args.configFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Error loading config: not found");
            }
        }

        if (args.port != null) {
            configBuilder.port(args.port);
        }

        return configBuilder.loadFromEnv().build();
    }

    public static void main(String[] argv) {
        Args args = new Args();
        JCommander jcommander = JCommander.newBuilder().addObject(args).build();

        try {
            jcommander.parse(argv);
        } catch (ParameterException e) {
            System.err.printf("Command line arguments error: %s%n", e.getMessage());

            jcommander.setProgramName("amcrm");
            jcommander.usage();

            System.exit(1);
        }

        if (args.debug != null && args.debug) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        System.getProperties().setProperty("org.jooq.no-logo", "true");

        final Config config = configure(args);

        Api api = new Api(config);

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    log.info("Shutting down...");

                                    api.stop();

                                    log.info("Server stopped");
                                }));

        api.makeSureAtLeastAdminExists();

        api.start();
    }

    public enum Resource {
        PING("/ping"),
        CUSTOMERS("/customers"),
        USERS("/users"),
        OAUTH("/oauth");

        private final String value;

        Resource(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
