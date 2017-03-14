#!/bin/bash

initialCount=-1

while :
do
	rm -rf /home/pi/output.txt
	php demo.php >> /home/pi/output.txt
	result=$(cat /home/pi/output.txt | grep "string(1)")
	strToReplace='Total Unread Messages'
	curCount="${result/string(1)/$strToReplace}"
        if [ "$initialCount" != "$curCount" ] 
then 
	echo "value changed "
	initialCount=$curCount
	echo $curCount
	gcloud beta pubsub topics publish aerispi "$curCount"
	echo "published new message count to queue aerispi : $curCount"

fi
	echo '.'
	sleep 2
done