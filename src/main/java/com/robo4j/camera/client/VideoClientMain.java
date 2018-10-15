/*
 * Copyright (c) 2014, 2018, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.camera.client;

import com.robo4j.RoboBuilder;
import com.robo4j.RoboContext;
import com.robo4j.camera.client.unit.VideoConfigurationUnit;
import com.robo4j.configuration.Configuration;
import com.robo4j.configuration.ConfigurationBuilder;
import com.robo4j.units.rpi.camera.RaspividUnit;
import com.robo4j.util.SystemUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class VideoClientMain {

    public static void main(String[] args) throws Exception {
        InputStream systemIS;
        InputStream contextIS;

        switch (args.length) {
            case 0:
                systemIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4jSystem.xml");
                contextIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4j.xml");
                System.out.println("Default configuration used");
                break;
            case 1:
                systemIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4jSystem.xml");
                Path contextPath = Paths.get(args[0]);
                contextIS = Files.newInputStream(contextPath);
                System.out.println("Robo4j config file has been used: " + args[0]);
                break;
            case 2:
                Path systemPath2 = Paths.get(args[0]);
                Path contextPath2 = Paths.get(args[1]);
                systemIS = Files.newInputStream(systemPath2);
                contextIS = Files.newInputStream(contextPath2);
                System.out.println(String.format("Custom configuration used system: %s, context: %s", args[0], args[1]));
                break;
            default:
                System.out.println("Could not find the *.xml settings for the CameraClient!");
                System.out.println("java -jar camera.jar system.xml context.xml");
                System.exit(2);
                throw new IllegalStateException("see configuration");
        }

        RoboBuilder builder = new RoboBuilder(systemIS);
        builder.add(contextIS);
        RoboContext system = builder.build();
        system.start();

        System.out.println("State after start:");
        System.out.println(SystemUtil.printStateReport(system));

        System.out.println("Press enter to quit!");
        System.in.read();
        system.shutdown();

    }
}
