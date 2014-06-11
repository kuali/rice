#
# Copyright 2005-2014 The Kuali Foundation
#
# Licensed under the Educational Community License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.opensource.org/licenses/ecl2.php
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

if [ -z "$1" ]
then
    echo "Required parameter, testname"
    exit;
fi

export TESTNAME="$1"
export JMETER_PATH="${WORKSPACE}/apache-jmeter-2.11"

if [ -z "$2" ]
then
    export REPORT_RESPONSE_TIME="false"
else
    export REPORT_RESPONSE_TIME="$2"
fi

if [ -z "$3" ]
then
    export REPORT_THROUGHPUT="false"
else
    export REPORT_THROUGHPUT="$3"
fi

if [ -z "$4" ]
then
    export REPORT_HITS_PER_SECOND="false"
else
    export REPORT_HITS_PER_SECOND="$4"
fi

if [ -z "$5" ]
then
    export REPORT_THREADS="false"
else
    export REPORT_THREADS="$5"
fi

if [ -z "$6" ]
then
    export REPORT_PERFMON="false"
else
    export REPORT_PERFMON="$6"
fi

if [ -z "$7" ]
then
    export REPORT_LATENCIES="false"
else
    export REPORT_LATENCIES="$7"
fi

if [ -z "$8" ]
then
    export REPORT_RESPONSE_CODES="false"
else
    export REPORT_RESPONSE_CODES="$8"
fi



echo "Params: RESPONSE_TIME:$REPORT_RESPONSE_TIME , THROUGHPUT:$REPORT_THROUGHPUT , HITS_PER_SEC:$REPORT_HITS_PER_SECOND , THREADS:$REPORT_THREADS, PERFMON:$REPORT_PERFMON, LATENCIES:$REPORT_LATENCIES, RESPONSE_CODES:$REPORT_RESPONSE_CODES"

# RUN JMETER and have test and outputs in the directory this is run from
# http://code.google.com/p/jmeter-plugins/wiki/JMeterPluginsCMD
if [ "$REPORT_RESPONSE_TIME" = "true" ]
then
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ResponseTimesOverTime.png --input-jtl ${TESTNAME}.jtl --plugin-type ResponseTimesOverTime --width 800 --height 600
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ResponseTimesDistribution.png --input-jtl ${TESTNAME}.jtl --plugin-type ResponseTimesDistribution --width 800 --height 600
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ResponseTimesPercentiles.png --input-jtl ${TESTNAME}.jtl --plugin-type ResponseTimesPercentiles --width 800 --height 600
fi

if [ "$REPORT_THROUGHPUT" = "true" ]
then
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ThroughputOverTime.png --input-jtl ${TESTNAME}.jtl --plugin-type ThroughputOverTime --width 800 --height 600
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}BytesThroughputOverTime.png --input-jtl ${TESTNAME}.jtl --plugin-type BytesThroughputOverTime --width 800 --height 600
fi

if [ "$REPORT_HITS_PER_SECOND" = "true" ]
then
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}HitsPerSecond.png --input-jtl ${TESTNAME}.jtl --plugin-type HitsPerSecond --width 800 --height 600
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}TransactionsPerSecond.png --input-jtl ${TESTNAME}.jtl --plugin-type TransactionsPerSecond --width 800 --height 600
fi

if [ "$REPORT_THREADS" = "true" ]
then
#   ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ThroughputVsThreads.png --input-jtl ${TESTNAME}.jtl --plugin-type ThroughputVsThreads --width 800 --height 600
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}TimesVsThreads.png --input-jtl ${TESTNAME}.jtl --plugin-type TimesVsThreads --width 800 --height 600
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ThreadsStateOverTime.png --input-jtl ${TESTNAME}.jtl --plugin-type ThreadsStateOverTime --width 800 --height 600
fi

if [ "$REPORT_PERFMON" = "true" ]
then
    ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}PerfMon.png --input-jtl ${TESTNAME}.jtl --plugin-type PerfMon --width 800 --height 600
#   ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}PageDataExtractorOverTime.png --input-jtl${TESTNAME}.jtl --plugin-type PageDataExtractorOverTime --width 800 --height 600
fi

if [ "$REPORT_LATENCIES" = "true" ]
then
   ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}LatenciesOverTime.png --input-jtl ${TESTNAME}.jtl --plugin-type LatenciesOverTime --width 800 --height 600
fi

if [ "$REPORT_RESPONSE_CODES" = "true" ]
then
   ${JMETER_PATH}/lib/ext/JMeterPluginsCMD.sh --generate-png ${TESTNAME}ResponseCodesPerSecond.png --input-jtl ${TESTNAME}.jtl --plugin-type ResponseCodesPerSecond --width 800 --height 600
fi



