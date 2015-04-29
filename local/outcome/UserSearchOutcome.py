import sys
from collections  import defaultdict
import math
reload(sys)
sys.setdefaultencoding('utf-8')

infile = sys.argv[1]
outfile = sys.argv[2]
dfflag = sys.argv[3].strip()


df = defaultdict(lambda:999999999)
for l in open('../idf/df.txt'):
    segs = l.strip().split('\t')
    if len(segs)==2:
        df[segs[0]] = int(segs[1])
# count[sid] [topic] [length] [d]=0
count = defaultdict(lambda:defaultdict(lambda:defaultdict(lambda:defaultdict(lambda:0))))

for l in open(infile).readlines():
    segs = l.strip().split('@_@')
    if len(segs) ==6:
        sid = int(segs[1])
        topic = int(segs[2])
        length = int(segs[3])
        ngram = segs[4]
        weight = segs[5]
        if dfflag =='df':
            count[sid][topic][length][ngram]+=(math.log(float(weight))+1)/float(df[ngram])
        if dfflag =='ndf':
            count[sid][topic][length][ngram]+=math.log(float(weight))+1
    else:
        print len(segs),l


def sort_by_value(d):
    items=d.items()
    backitems=[[v[1],v[0]] for v in items]
    backitems.sort(reverse=True)
    return [ backitems[i][1] for i in range(0,len(backitems))]

fout = open(outfile,'w')
for s in count.keys():
    for t in count[s].keys():
        fout.write(str(s)+'\t'+str(t)+'\t')
        for l in range(1,9,1):
            fout.write(''.join(sort_by_value(count[s][t][l])[0:10])+' ')
        fout.write('\n')
fout.close()