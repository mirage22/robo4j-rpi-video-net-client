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

package com.robo4j.camera.client.unit;

import com.robo4j.BlockingTrait;
import com.robo4j.ConfigurationException;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.camera.client.VideoClientException;
import com.robo4j.configuration.Configuration;
import com.robo4j.socket.http.codec.VideoConfigMessage;
import com.robo4j.units.rpi.camera.RaspividRequest;
import com.robo4j.units.rpi.camera.RaspividRequestType;
import com.robo4j.units.rpi.camera.RpiCameraProperty;

/**
 * VideoConfigurationUnit configures {@link com.robo4j.units.rpi.camera.RaspividUnit}
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
@BlockingTrait
public class VideoConfigurationUnit extends RoboUnit<VideoConfigMessage> {

    public static final String NAME = "videoConfigUnit";
    private static final String PROPERTY_TARGET = "target";
    private String target;

    public VideoConfigurationUnit(RoboContext context, String id) {
        super(VideoConfigMessage.class, context, id);
    }

    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        target = configuration.getString(PROPERTY_TARGET, null);
        if (target == null) {
            throw ConfigurationException.createMissingConfigNameException(PROPERTY_TARGET);
        }
    }

    @Override
    public void onMessage(VideoConfigMessage message) {
        if(message.getType() == null){
            throw new VideoClientException("not valid camera message: " + message);
        }
        switch (message.getType()) {
            case CONFIG:
                System.out.println("CONFIG MESSAGE");
                sendConfigMessage(message);
                break;
            case START:
                System.out.println("START MESSAGE");
                getContext().getReference(target).sendMessage(new RaspividRequest(true, RaspividRequestType.START));
                break;
            case STOP:
                System.out.println("STOP MESSAGE");
                getContext().getReference(target).sendMessage(new RaspividRequest(false, RaspividRequestType.STOP));
                break;
            default:
                throw new VideoClientException("not implemented: " + message);
        }


    }

    private void sendConfigMessage(VideoConfigMessage message) {
        final RaspividRequest request = new RaspividRequest(true, RaspividRequestType.CONFIG)
                .put(RpiCameraProperty.WIDTH, message.getWidth().toString())
                .put(RpiCameraProperty.HEIGHT, message.getHeight().toString())
                .put(RpiCameraProperty.ROTATION, message.getRotation().toString())
                .put(RpiCameraProperty.TIMEOUT, message.getTimeout().toString());
        getContext().getReference(target).sendMessage(request);
    }
}
