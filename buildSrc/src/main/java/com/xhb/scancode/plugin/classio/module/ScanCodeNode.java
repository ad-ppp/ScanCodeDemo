package com.xhb.scancode.plugin.classio.module;

public class ScanCodeNode {

    /**
     * field : {"owner":"java.lang.System","name":"out","desc":"Ljava/io/PrintStream;"}
     * method : {"owner":"java/io/PrintStream","name":"println","desc":"(Ljava/lang/String;)V"}
     */

    private FieldNode field;
    private MethodNode method;

    public FieldNode getField() {
        return field;
    }

    public void setField(FieldNode field) {
        this.field = field;
    }

    public MethodNode getMethod() {
        return method;
    }

    public void setMethod(MethodNode method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ScanCodeNode{" +
                "field=" + field +
                ", method=" + method +
                '}';
    }

    public static class FieldNode {
        /**
         * owner : java.lang.System
         * name : out
         * desc : Ljava/io/PrintStream;
         */

        private String owner;
        private String name;
        private String desc;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return "FieldNode{" +
                    "owner='" + owner + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }

    public static class MethodNode {
        /**
         * owner : java/io/PrintStream
         * name : println
         * desc : (Ljava/lang/String;)V
         */

        private String owner;
        private String name;
        private String desc;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return "MethodNode{" +
                    "owner='" + owner + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}
