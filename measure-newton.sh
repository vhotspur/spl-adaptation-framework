#!/bin/sh

#
# Copyright 2012 Charles University in Prague
# Copyright 2012 Vojtech Horky
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


LOOPS="$1"
shift

[ -z "$LOOPS" ] && exit 1

case "$1" in
	--std-agent=*)
		EXTRA_OPTS="-javaagent:out/jar/spl-agent.jar=spl.shutdown.class=cz.cuni.mff.d3s.spl.example.newton.checker.SlaChecker:`echo \"$1\" | cut '-d=' -f 2-`"
		shift
		;;
	--std-agent)
		EXTRA_OPTS="-javaagent:out/jar/spl-agent.jar=spl.shutdown.class=cz.cuni.mff.d3s.spl.example.newton.checker.SlaChecker"
		shift
		;;
	--std-agent-bare)
		EXTRA_OPTS="-javaagent:out/jar/spl-agent.jar"
		shift
		;;
	*)
		;;
esac

MY_CLASSPATH="out/classes:lib/asm-debug-all-4.0.jar:lib/commons-math-2.2.jar"

TASKSET_=`which taskset 2>/dev/null`
TASKSET="$TASKSET"

randomize_taskset() {
	if ! [ -z "$TASKSET_" ]; then
		max_proc=`cat /proc/cpuinfo 2>/dev/null | grep '^processor.*:' | cut '-d:' -f 2 | sort | tail -n 1`
		[ -z "$max_proc" ] && max_proc=0
		if [ "$max_proc" -eq 0 ]; then
			proc=0
		else
			proc=$(( $RANDOM % (1 + $max_proc ) ))
		fi
		TASKSET="$TASKSET_ -c $proc";
	fi
}

SUM="0"
DATA_VECTOR="NaN";
for i in `seq 1 $LOOPS`; do
	echo "Starting loop $i (out of $LOOPS)"
	randomize_taskset
	$TASKSET java -ea -classpath "$MY_CLASSPATH" $EXTRA_OPTS cz.cuni.mff.d3s.spl.example.newton.app.Main "$@" | tee $$.tmp || break
	MEASURED=`sed -n 's/.*loops of solving.*[(]\([0-9]*\)ms.*/\1/p' <$$.tmp`
	rm -f $$.tmp
	SUM="$SUM+$MEASURED"
	DATA_VECTOR="$DATA_VECTOR,$MEASURED"
done
rm -f $$.tmp
AVG=`echo '(' "$SUM" ')/' "$LOOPS" | bc`
STATS=`cat <<R_SCRIPT | Rscript --vanilla /dev/stdin
format.stats <- function(x) {
	x.mean <- mean(x, na.rm=TRUE)
	x.sd <- sd(x, na.rm=TRUE)

	sprintf(fmt="mean=%0.1fms (%0.2f)", x.mean, x.sd)
}

x <- c($DATA_VECTOR)
x.len <- length(x)
x.part <- x[(x.len/5)+1:x.len]
# print(x)
# print(x.part)
writeLines(sprintf(fmt="all: %s   skip first ones: %s", format.stats(x), format.stats(x.part)), sep="")
R_SCRIPT`
echo ">>>>> $STATS."
