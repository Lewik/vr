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