__author__ = 'luocheng'


import os
fout = open('run.sh','w')
for f in os.listdir('./bleu'):
    fout.write('python CorrelationAnlysis.py ./bleu/'+f+"\n")
fout.close()