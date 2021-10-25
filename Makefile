.SUFFIXES:.java .class

.java.class:
	/usr/bin/javac -Xlint:unchecked $<

all: Server.class Client.class

Server.class: Server.java

Client.class: Client.java
