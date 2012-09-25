#!/bin/sh


LOOPS="$1"
shift

[ -z "$LOOPS" ] && exit 1

run_measure() {
	echo "$1"
	shift
	./measure-newton.sh "$LOOPS" "$@" 2>&1 | tail -n 1 | sed 's/^[>]*//'
}

run_measure "Without any agent"
run_measure "With bare agent" --std-agent-bare
run_measure "With agent, without measuring" --std-agent=no-measuring
run_measure "With agent, with measuring" --std-agent
