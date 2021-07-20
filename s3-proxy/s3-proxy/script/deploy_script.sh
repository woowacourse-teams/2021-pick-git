#!/bin/bash

if [ $# -eq 0 ]
then
	echo "Usage: auto_build [options]"
	echo "	Options"
	echo "		-c (String)	certificate"
	echo "		-p (int)	port (default 22)"
	echo "		-h (String)	host"
	echo "		-l (String)	location (defualt /home/ubuntu))"
	echo "		-u (String)	user (defualt ubuntu)"
	exit 1
fi

CERTIFICATE_PATH=""
PORT=22
HOST=""
USER="ubuntu"
LOCATION="/home/ubuntu"
DEPLOY=""

#parse options
while (( "$#" )); do
	case "$1" in
		-c|--certificate)
			if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
				CERTIFICATE_PATH=$2
				shift 2
			fi
			;;
		-p|--port)
			if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
				PORT=$2
				shift 2
			fi
			;;
		-h|--host)
			if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
				HOST=$2
				shift 2
			fi
			;;
		-l|--location)
			if [ -n "$2" ]  && [ ${2:0:1} != "-" ]; then
				LOCATION=$2
				shift 2
			fi
			;;
		-u|--user)
			if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
				USER=$2
			fi
			;;
		-d|--deploy)
			if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
				DEPLOY=$2
				shift 2
			fi
			;;
	esac
done

if [ ! -f "$CERTIFICATE_PATH" ]; then
	echo "Error: certificate is not exist"
	exit 1
fi

if [ -z $HOST ]; then
	echo "Error: host is required"
	exit 1
fi

if [ -z $DEPLOY ]; then
	echo "Error: deploy option is required"
	exit 1
fi

##remove plain jar
rm ../build/libs/*plain*.jar

#migration
JAR_PATH=$(find ../build/libs -name "*.jar")
scp -i $CERTIFICATE_PATH $JAR_PATH $USER@$HOST:$LOCATION

JAR_NAME=${JAR_PATH##*/}

ssh -i $CERTIFICATE_PATH -l $USER $HOST "PID=\$(ps -p \$(lsof -ti tcp:8080) o pid=); kill -9 \$PID; sleep 5; nohup java -Dspring.profiles.active=$DEPLOY -jar $LOCATION/$JAR_NAME > pickgit.out 2> pickgit.err < /dev/null &"

echo "deploy finished"
