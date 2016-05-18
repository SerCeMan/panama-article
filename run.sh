LPATH="src/jmh/java"
./gradlew compileJmhJava && /home/serce/tmp/panama/panama/build/linux-x86_64-normal-server-release/images/jdk/bin/java -Djava.library.path=$LPATH  -cp build/classes/jmh/ me.serce.panex.ChecksumBenchmark
