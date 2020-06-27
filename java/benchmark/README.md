# Benchmark results

This benchmark was run on a 2.8 GHz 7th gen Intel Core i7.

The benchmark was run by reading 100, 1000, and 10000 properties from a file.
The value of each of these properties is changed once per second
(by regenerating the value in an Executor thread).

```
Mean(100 props)    =    2.403 microseconds/op
P100(100 props)    = 1480.704 microseconds/op

Mean(1000 props)   =   20.354 microseconds/op
P100(1000 props)   = 4669.440 microseconds/op

Mean(10000 props)  =  222.944 microseconds/op
P100(10000 props)  = 8585.216 microseconds/op
```

The mean sample time increases linearly with the number of the props that are read,
however, the 100th percentile (or the time it takes to read all the props) does not change
its order of magnitude.

Even in a high-traffic application, it is unlikely that such a large number of props
will be read and that *all of the props' values* will change each second.

While this is not an extensive benchmark and it doesn't take into account all the possible cases
nor different prop combinations, it proves that even on a regular (to be read, "not a server")
processor, the performance impact of reading properties with `Props` is negligible.

**It is important to note that this library was not built with low-latency applications in mind.**
If, however, you are interested in applying `props` to such an app,
[please open an issue](https://github.com/MihaiBojin/props/issues/new) and add all the necessary
details. Thank you! 


### 100 properties

```
mean =      2.403 ±(99.9%) 0.015 us/op

Benchmark                                                                          Mode      Cnt     Score   Error  Units
GenericBenchmarks.readPropsUpdatedEachSecond                                     sample  1336838     2.403 ± 0.015  us/op
GenericBenchmarks.readPropsUpdatedEachSecond:readPropsUpdatedEachSecond·p0.9999  sample             56.576          us/op
GenericBenchmarks.readPropsUpdatedEachSecond:readPropsUpdatedEachSecond·p1.00    sample           1480.704          us/op
```

# Benchmarks with more properties

## 1000 properties

```
mean =     20.354 ±(99.9%) 0.033 us/op
Benchmark                                                                          Mode      Cnt     Score   Error  Units
GenericBenchmarks.readPropsUpdatedEachSecond                                     sample  1227050    20.354 ± 0.033  us/op
GenericBenchmarks.readPropsUpdatedEachSecond:readPropsUpdatedEachSecond·p0.9999  sample            636.206          us/op
GenericBenchmarks.readPropsUpdatedEachSecond:readPropsUpdatedEachSecond·p1.00    sample           4669.440          us/op
```

## 10000 properties

```
mean =    222.944 ±(99.9%) 0.337 us/op
Benchmark                                                                          Mode     Cnt     Score   Error  Units
GenericBenchmarks.readPropsUpdatedEachSecond                                     sample  224102   222.944 ± 0.337  us/op
GenericBenchmarks.readPropsUpdatedEachSecond:readPropsUpdatedEachSecond·p0.9999  sample          1506.015          us/op
GenericBenchmarks.readPropsUpdatedEachSecond:readPropsUpdatedEachSecond·p1.00    sample          8585.216          us/op
```