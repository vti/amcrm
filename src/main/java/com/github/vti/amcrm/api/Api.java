package com.github.vti.amcrm.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.file.FileService;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.api.service.AuthenticationService;
import com.github.vti.amcrm.api.service.CustomerService;
import com.github.vti.amcrm.api.service.PingService;
import com.github.vti.amcrm.api.service.UserService;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.infra.photo.LocalPhotoStorage;
import com.github.vti.amcrm.infra.photo.PhotoStorage;
import com.github.vti.amcrm.infra.registry.RegistryFactory;
import com.github.vti.amcrm.infra.registry.ViewRegistry;

class Args {
    @Parameter(names = "--config", description = "Path to config file")
    public String configFile = null;

    @Parameter(names = "--port", description = "Server port")
    public Integer port = null;
}

public final class Api {
    private static final Logger log = LogManager.getLogger(Api.class);

    private final Config config;
    private Server server;

    public Api(Config config) {
        this.config = config;
    }

    public void start() {
        final RegistryFactory registryFactory =
                new RegistryFactory(config.getStorage(), config.getBaseUrl().toString());
        final Path publicDir = Paths.get("public");
        final PhotoStorage photoStorage =
                new LocalPhotoStorage(Paths.get(publicDir.toString()), Paths.get("customer"));

        log.info("Loaded configuration: {}", config.toString());

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
                        (delegate, ctx, req) -> new AuthenticationService(delegate).serve(ctx, req))
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

        api.start();
    }

    public enum Resource {
        PING("/ping"),
        CUSTOMERS("/customers"),
        USERS("/users");

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
