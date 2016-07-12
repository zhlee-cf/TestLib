package com.open.im.bean;

/**
 * OIM格式收到的消息对应解析bean
 * Created by Administrator on 2016/4/7.
 */
public class ReceiveBean extends ProtocalObj{

    private String namespace;
    private String version;
    private String type;
    private long size;

    private PropertiesBean properties;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public PropertiesBean getProperties() {
        return properties;
    }

    public void setProperties(PropertiesBean properties) {
        this.properties = properties;
    }

    public static class PropertiesBean extends ProtocalObj{
        private double longitude;
        private double latitude;
        private double accuracy;
        private String manner;
        private String address;
        private String description;
        private String resolution;
        private String thumbnail;
        private long length;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(double accuracy) {
            this.accuracy = accuracy;
        }

        public String getManner() {
            return manner;
        }

        public void setManner(String manner) {
            this.manner = manner;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        @Override
        public String toString() {
            return "PropertiesBean{" +
                    "longitude=" + longitude +
                    ", latitude=" + latitude +
                    ", accuracy=" + accuracy +
                    ", manner='" + manner + '\'' +
                    ", address='" + address + '\'' +
                    ", description='" + description + '\'' +
                    ", resolution='" + resolution + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", length=" + length +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ReceiveBean{" +
                "namespace='" + namespace + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", properties=" + properties +
                '}';
    }
}
