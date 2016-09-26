In this project, HBase runs in pseudo-distribution mode
Configurations are troublesome but indispensable.

//following are setups and configurations I did


//1.setups for Hadoop

sudo adduser hadoop
sudo usermod -G sudo hadoop
sudo apt-get install openssh-server
sudo apt-get install rsync
//install a jdk that can work for your hadoop's version if you don't have one.

sudo -l hadoop
ssh-keygen -t rsa -P ""
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
//download hadoop-2.6.4 from apache's website, unzip, move to /usr/local/hadoop, chmod to 774
vim /home/hadoop/.bashrc  (add some paths at the end of .bashrc)
source ~/.bashrc


//2.configurations for Hadoop(pseudo-distribution mode)

cd /usr/local/hadoop/etc/hadoop
vim core-site.xml
vim hdfs-site.xml
vim mapred-site.xml
vim yarn-site.xml
vim hadoop-env.sh (export JAVA_HOME=${a jdk's path})

su -l hadoop
hadoop namenode -format

start-dfs.sh
start-yarn.sh

check http://localhost:8088  for ResourceManager web ui page
check http://localhost:50070 for HDFS web ui page


//3.a word count test

hadoop dfs -mkdir /user
hadoop dfs -mkdir /user/hadoop
hadoop dfs -mkdir /user/hadoop/input
hadoop dfs -put /etc/protocols /user/hadoop/input  (upload a file in linux file system to hdfs)
cd $HADOOP_INSTALL
bin/hadoop jar share/hadoop/mapreduce/sources/hadoop-mapreduce-examples-2.6.4-sources.jar org.ahache.hadoop.examples.WordCount input output
hadoop dfs -cat /user/hadoop/output/*  (check the results of word counting)

stop-dfs.sh
stop-yarn.sh


//4.setups and configurations for HBase(pseudo-distribution mode)

//download HBase-1.2.2 from apache's web
vim hbase-site.xml
vim ${HBASE_HOME}/conf/hbase-env.sh


//5.start HBase

start-dfs.sh
start-yarn.sh
start-hbase.sh
jps  // (NameNode + SecondaryNameNode + DataNode) + (ResourceManager + NodeManager) + (HMaster + HRegionServer + HQuorumPeer)

hbase shell
create 'testTable','cf'
...
...


//6.the end
stop-hbase.sh
stop-dfs.sh
stop-yarn.sh


