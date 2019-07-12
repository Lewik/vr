.ONESHELL:
.PHONY: build

all:
	echo "Choose task"


build:
	set -e

	cd jvmServer
	./../gradlew bootJar
	cd ../jvmClient
	./../gradlew bootJar
	cd ..

copyToWin:
	set -e

	sudo cp jvmServer/build/libs/jvmServer-1.0-SNAPSHOT.jar ~/shared/vr/server/server.jar
	sudo cp jvmClient/build/libs/jvmClient-1.0-SNAPSHOT.jar ~/shared/vr/client/client.jar
	sudo cp jvmClient/src/main/resources/application.properties ~/shared/vr/client/application.properties

toDemo:
	scp jvmServer-1.0-SNAPSHOT.jar demo:/srv/vr
