#! /bin/sh

##############################################################################
##
##  application start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/.." >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

# Variables
APP_NAME=mahu-dashboard
PIDFILE=/var/run/${APP_NAME}.pid
CMD=mahu-dashboard

pid=""

getpid() {
    if  [ -f "$PIDFILE" ]
    then
        if [ -r "$PIDFILE" ]
        then
            pid=`cat "$PIDFILE"`
            if [ "X$pid" != "X" ]
            then
                pidtest=`ps -p $pid -o args | grep java | tail -1`
                if [ "X$pidtest" = "X" ]
                then
                    rm -f "$PIDFILE"
                    echo "Delete stale pid file"
                    pid=""
                fi
            fi
        fi
    fi
}

testpid() {
    pid=`ps -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`
    if [ "X$pid" = "X" ]
    then
        # Process is gone so remove the pid file.
        rm -f $PIDFILE
        pid=""
    fi
}

start() {
    echo "Starting application..."
    getpid

    if [ "X$pid" = "X" ]
    then
        nohup sh $CMD > /dev/null 2>&1 &
        echo $! > $PIDFILE
    else
        echo "Application already running."
        exit 1
    fi

    sleep 10
    getpid

    if [ "X$pid" != "X" ]
    then
        echo  "Application started!"
    else
        echo "Application did not start after 10 seconds. Check $LOG_PATH/$LOG_FILE"
    fi
}

stopit() {
    echo "Stopping application..."
    getpid
    if [ "X$pid" = "X" ]
    then
        echo "Application not running."
    else
        kill "$pid"
        if [ $? -ne 0 ]
        then
            echo "Unable to stop application."
            exit 1
        fi

        savepid=$pid
        CNT=0
        TOTCNT=0
        while [ "X$pid" != "X" ]
        do
            # Show a waiting message every 5 seconds.
            if [ "$CNT" -lt "5" ]
            then
                CNT=`expr $CNT + 1`
            else
                echo "Waiting for application to exit..."
                CNT=0
            fi
            TOTCNT=`expr $TOTCNT + 1`

            sleep 1

            testpid
        done

        pid=$savepid
        testpid
        if [ "X$pid" != "X" ]
        then
            echo "Failed to stop application."
            exit 1
        else
            echo "Stopped application."
        fi
    fi
}

status() {
    getpid
    if [ "X$pid" = "X" ]
    then
        echo "Application is not running."
        exit 1
    else
        echo "Application is running ($pid)."
        exit 0
    fi
}

case "$1" in
    'start')
        start
        ;;

    'stop')
        stopit
        ;;

    'restart')
        stopit
        start
        ;;
    'status')
        status
        ;;
    *)
    echo "Usage: $0 { start | stop | restart | status }"
    exit 1
esac

exit 0
