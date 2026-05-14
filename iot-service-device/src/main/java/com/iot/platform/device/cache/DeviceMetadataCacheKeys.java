package com.iot.platform.device.cache;

public final class DeviceMetadataCacheKeys {

    private DeviceMetadataCacheKeys() {
    }

    public static String typeList(String prefix) {
        return prefix + "type:list";
    }

    public static String type(String prefix, Long typeId) {
        return prefix + "type:" + typeId;
    }

    public static String pointListByType(String prefix, Long typeId) {
        return prefix + "point:type:" + typeId + ":list";
    }

    public static String point(String prefix, Long pointId) {
        return prefix + "point:" + pointId;
    }
}

