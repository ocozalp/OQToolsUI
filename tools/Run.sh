export RUN_NAME=$1;
export LOG_DIR=/usr/openquake/engine/Runs/Logs/$RUN_NAME;
export MAIN_WORK_DIR=/usr/openquake/engine/Runs/ConfigGem/$RUN_NAME;
export LOG_FILE=$LOG_DIR/openquake;
export OUT_LOG_FILE=$LOG_FILE.out;
export ERR_LOG_FILE=$LOG_FILE.err;

if [ -e $MAIN_WORK_DIR ];
then

    #create log directory
    if [ ! -e $LOG_DIR ];
    then
        mkdir -p $LOG_DIR;
    fi

    #create log files
    touch $OUT_LOG_FILE;
    touch $ERR_LOG_FILE;

    #go back to working directory
    cd $MAIN_WORK_DIR;

    echo `date +"%H:%M:%S-%d/%m/%G"`": Starting run" >> $OUT_LOG_FILE;
    openquake --run-hazard=./job.ini --exports=xml --log-level=error 1>>$OUT_LOG_FILE 2>>$ERR_LOG_FILE;
    echo `date +"%H:%M:%S-%d/%m/%G"`": Finished run" >> $OUT_LOG_FILE;

else
    echo "ERROR : Run directory does not exist! : "$MAIN_WORK_DIR;
fi