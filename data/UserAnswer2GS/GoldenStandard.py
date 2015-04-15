import sys
from collections  import defaultdict
reload(sys)
sys.setdefaultencoding('utf-8')

infile = sys.argv[1]
outfile = sys.argv[2]

# already = defaultdict(lambda:'')
# for l in open('../topics.csv'):
#     segs = l.strip().split(',')
#     tid = int(segs[0])
#     for s in segs[1:]:
#         already[tid] += s

# count [topic] [length] [d]=0
count = defaultdict(lambda:defaultdict(lambda:defaultdict(lambda:0)))

for l in open(infile).readlines():
    segs = l.strip().split('::')
    topic = int(segs[1])
    length = int(segs[2])
    ngram = segs[3]
    # if ngram not in already[topic]:
    count[topic][length][ngram]+=1
    # else:
    #     print 'in'


def sort_by_value(d):
    items=d.items()
    backitems=[[v[1],v[0]] for v in items]
    backitems.sort(reverse=True)
    return [ backitems[i][1] for i in range(0,len(backitems))]

fout = open(outfile,'w')
for t in count.keys():
    fout.write(str(t)+'\t')
    for l in range(1,9,1):
        fout.write(' '.join(sort_by_value(count[t][l])[0:10])+' ')
    fout.write('\n')
fout.close()
