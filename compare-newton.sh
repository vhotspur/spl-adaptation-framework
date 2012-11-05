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

run_measure() {
	echo "$1"
	shift
	./measure-newton.sh "$LOOPS" "$@" 2>&1 | tail -n 1 | sed 's/^[>]*//'
}

run_measure "Without any agent"
run_measure "With bare agent" --std-agent-bare
run_measure "With agent, without measuring" --std-agent=no-measuring
run_measure "With agent, with measuring (unobtrusive)" "--std-agent=,skip.factor=1000"
run_measure "With agent, with measuring" "--std-agent"
run_measure "With agent, with measuring (intensive)" "--std-agent=,skip.factor=2"
run_measure "With agent, with measuring (very intensive)" "--std-agent=,skip.factor=1"
