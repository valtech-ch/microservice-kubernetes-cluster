#!/usr/bin/env bash

git diff --name-only HEAD $(git merge-base HEAD origin/develop) | grep .java | grep --invert-match src/test/java | grep --invert-match generated | grep --invert-match Dto.java | sed -E "s/^.*src\/main\/java\///g"
