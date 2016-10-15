# SpatialDataWithHBase
Storing and retrieving spatial data in Hbase

This is the curriculum design for software engineering

Programming language: Java

Operating system: deepin-15.2 (a beautiful desktop Linux distribution contributed by Chinese)

File system: HDFS-2.6.4 (pseudo-distribution mode)

Database: HBase-1.2.2

**Spatial data:** (x, y)

**Rowkey:** it will be 16 charecters

 1. (x0, y0) ---Hilbert curve---> hilbertCode. 6 charecters
 2. assign an ID to (x0, y0). 10 charecters
 3. rowkey = hilbertCode + ID. 16 charecters

**HBase schema:**

Column Family : Point

Qualifier : ID, X, Y

Time Stamp: system default

eg.a spatial point (222.222, 333.333), assigned an ID=1. Shoud put 3 records in HBase

| Rowkey         | Point:ID | Point:X | Point:Y |
|:--------------:|:--------:|:-------:|:-------:|
|0123450000000001|    1     |         |         |
|0123450000000001|          | 222.222 |         |
|0123450000000001|          |         | 333.333 |

**Range query:**
 1. range (x1, y1)---(x2, y2)
 2. stage one: coarse filter, scan by rowkey, determined by hilbert curve values of all blocks in range (x1, y1)---(x2, y2)
 3. stage two: fine filter, single column value filter, determined by x1, x2, y1, y2;
