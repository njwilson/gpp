#!/usr/bin/env python

# Reads output of bin/explore_params.sh
# Usage: python plot_params <input.csv> [<figure.eps>]

import matplotlib.pyplot as plt
import pandas


def main(args):
    data_file = args[0]
    output_file = None
    if len(args) > 1:
        output_file = args[1]

    # Read the data
    pandas.read_csv(data_file, header=None)
    df = pandas.read_csv(args[0], header=None)
    df.columns = ('C', 'Accuracy', 'F1')
    df = df.sort(columns='C')

    # Make each line peak at 100
    for col in ('Accuracy', 'F1'):
        df[col] = df[col] - df[col].max() + 100.0
    df['Weighted'] = 0.3 * df['Accuracy'] + 0.7 * df['F1']
    df['Weighted'] = df['Weighted'] - df['Weighted'].max() + 100.0

    # Print the row with the max weighted value
    print(df.ix[df.idxmax()['Weighted']])

    # Plot the data
    df.plot(x='C', ylim=(95, 100.1))
    if output_file is None:
        plt.show(output_file)
    else:
        plt.savefig(output_file)
        print("Saved to {0}".format(output_file))


if __name__ == '__main__':
    import sys
    main(sys.argv[1:])
