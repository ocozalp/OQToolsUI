export RUN_NAME=$1;
export RUNNING_DIR=`pwd`;
export LOG_DIR=$RUNNING_DIR/Logs/$RUN_NAME;

export LOG_FILE_DATE=`date +"%G_%m_%d__%H_%M_%S"`;
export LOG_FILE=$LOG_DIR/OQ_$LOG_FILE_DATE;
export OUT_LOG_FILE=$LOG_FILE.out
export ERR_LOG_FILE=$LOG_FILE.err
export CELERY_LOG_FILE=$LOG_DIR/CELERY_$LOG_FILE_DATE.log;

export MAIN_WORK_DIR=$RUNNING_DIR/ConfigGem/$RUN_NAME;
export TOTAL_FILE_COUNT=0;
export COUNTER=0;
export PROCESSES_TO_KILL=''
export NUMBER_OF_PROCESSES=0;

#create log directory
if [ ! -e $LOG_DIR ];
then
mkdir -p $LOG_DIR;
fi

#create log files
#touch $CELERY_LOG_FILE;
touch $OUT_LOG_FILE;
touch $ERR_LOG_FILE;

#get the list of existing celeryd processes
#for existingProcess in `ps aux | grep celeryd | grep -v 'grep' | awk '{print $2}'`; do
#       NUMBER_OF_PROCESSES=$((NUMBER_OF_PROCESSES + 1));
#       PROCESSES_TO_KILL=$PROCESSES_TO_KILL' '$existingProcess;
#done

#kill existing celeryd processes
#if [ $NUMBER_OF_PROCESSES -gt 0 ];
#then
#       echo `date +"%H:%M:%S-%d/%m/%G"`": Killing "$NUMBER_OF_PROCESSES" existing celeryd process(es) : ";
#       echo `date +"%H:%M:%S-%d/%m/%G"`": Killing "$NUMBER_OF_PROCESSES" existing celeryd process(es) : " >> $OUT_LOG_FILE;
#       killall -9 celeryd;
#fi;

#go to openquake directory for celeryconfig.py file
#cd /usr/openquake

#start new celeryd job
#celeryd --logfile $CELERY_LOG_FILE -c 50 --loglevel ERROR 1> CELERY.out 2>CELERY.err &

#go back to working directory
cd $RUNNING_DIR

echo `date +"%H:%M:%S-%d/%m/%G"`": Starting run" >> $OUT_LOG_FILE;
echo `date +"%H:%M:%S-%d/%m/%G"`": Calculating total file count" >> $OUT_LOG_FILE;

for sourceModel in $MAIN_WORK_DIR/*; do
        for configFile in $sourceModel/job.ini; do
                TOTAL_FILE_COUNT=$((TOTAL_FILE_COUNT + 1));
        done
done


for sourceModel in $MAIN_WORK_DIR/*; do
        for configFile in $sourceModel/job.ini; do
                COUNTER=$((COUNTER + 1));
                echo `date +"%H:%M:%S-%d/%m/%G"`": Executing config file ("$COUNTER" of "$TOTAL_FILE_COUNT") : "$configFile >> $OUT_LOG_FILE;
                #openquake --config-file $configFile --output-type=xml --log-level=debug 1>>$OUT_LOG_FILE 2>>$ERR_LOG_FILE;
                openquake --run-hazard=$configFile --exports=xml --no-distribute --log-level=debug 1>>$OUT_LOG_FILE 2>>$ERR_LOG_FILE;
                echo `date +"%H:%M:%S-%d/%m/%G"`": Finished execution for file : "$configFile >> $OUT_LOG_FILE;
        done
done

echo `date +"%H:%M:%S-%d/%m/%G"`": Finished run" >> $OUT_LOG_FILE;

#killing celeryd processes
#echo `date +"%H:%M:%S-%d/%m/%G"`": Killing  existing celeryd process(es) : ";
#echo `date +"%H:%M:%S-%d/%m/%G"`": Killing  existing celeryd process(es) : " >> $OUT_LOG_FILE;
#killall -9 celeryd;
