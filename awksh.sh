#!/bin/bash
path=`pwd`
in=$path"/awkTest.txt"
out=$path"/index.mots"

awk '{
		for (i=1; i<=NF; i++)
		mot[$i]++        # mot[$i]=mot[$i]+1
	}
END {
	for (i in mot)
	print i, mot[i]
}' $1 | sort -n -r 

