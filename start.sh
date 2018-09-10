#!/bin/bash

cd /home/apps/apidb

LIBDIR="lib/"
LIB="./classes"
for i in `ls $LIBDIR`
do
	LIB=$LIB:$LIBDIR$i;
	echo $i"\n"
done
echo ${LIB}



if [ ! -f app.pid ]
         then
java -Xmn1024m -Xmx1024m -verbose:gc -cp $LIB org.api.db.App conf/mdb.conf > out.txt 2>&1 & echo $! > app.pid
echo "done"
        else echo "udah jalan ? "
fi

