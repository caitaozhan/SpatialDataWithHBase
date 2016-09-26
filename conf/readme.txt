1.add a directory "/conf" in java project

2.cp $HBASE_HOME/conf/hbase-site.xml /conf

3.add "/conf" to CLASSPATH like this: Property -> Java Build Path -> Libraries -> Add Class Folder -> （find directory /conf）


hbase-site.xml then becomes the defualt configuration for a HBase instance  in java client application, i.e., java client then knows "the location" of a running HBase so that java client knows where to connect.
