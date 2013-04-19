#!/bin/bash

train_data="$1"
eval_data="$2"
method="$3"
seq_first="$4"
seq_inc="$5"
seq_last="$6"

for cost in `seq $seq_first $seq_inc $seq_last`; do
    output=`bin/gpp exp --train "$train_data" --eval "$eval_data" --method "$method" --cost $cost`
    accuracy=`echo "$output" |grep "Overall accuracy" | awk '{print $1}'`
    average_f1=`echo "$output" | grep "Average" | awk '{print $3}'`
    echo "${cost},${accuracy},${average_f1}"
done
