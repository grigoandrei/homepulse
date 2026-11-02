package org.homepulse.api.people;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class PeopleCli implements ApplicationRunner {
    private final PersonRepository people;
    private final DeviceRepository devices;
    private final DeviceTokens tokens;
    private final ConfigurableApplicationContext context;

    public PeopleCli(PersonRepository people, DeviceRepository devices, DeviceTokens tokens, ConfigurableApplicationContext context) {
        this.people = people;
        this.devices = devices;
        this.tokens = tokens;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> words = args.getNonOptionArgs();
        if (words.isEmpty()) {
            return;
        }

        int exitCode = switch (words.get(0)) {
            case "add-person" -> addPerson(words);
            case "add-device" -> addDevice(words);
            default -> {
                System.err.println("Unknown command: " + words.get(0));
                yield 1;
            }
        };
        System.exit(SpringApplication.exit(context, () -> exitCode));
    }

    private int addPerson(List<String> words) {
        if (words.size() != 2) {
            System.err.println("Usage: add-person NAME");
            return 1;
        }
        String name = words.get(1);
        if (people.findByName(name).isPresent()) {
            System.err.println("Person already exists: " + name);
            return 1;
        }

        Person person = people.save(new Person(name));
        System.out.println("Added person " + person.getName() + " with id " + person.getId());
        return 0;
    }

    private int addDevice(List<String> words) {
        if (words.size() != 4) {
            System.err.println("Usage: add-device NAME PLATFORM LABEL");
            return 1;
        }
        String name = words.get(1);
        String label = words.get(3);

        Optional<Person> person = people.findByName(name);
        if (person.isEmpty()) {
            System.err.println("No person named " + name);
            return 1;
        }

        Platform platform;
        try {
            platform = Platform.valueOf(words.get(2));
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown platform " + words.get(2) + ". Use one of " + Arrays.toString(Platform.values()));
            return 1;
        }

        String token = tokens.newToken();
        Device device = devices.save(new Device(person.get(), platform, label, tokens.hash(token), Instant.now()));

        System.out.println("Added device " + device.getLabel() + " with id " + device.getId());
        System.out.println("Token, shown once. Store it in the phone app now:");
        System.out.println(token);
        return 0;
    }
}
