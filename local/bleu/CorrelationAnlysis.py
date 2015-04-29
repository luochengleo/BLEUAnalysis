from collections import defaultdict
import sys

filename = sys.argv[1]

# stduent id -> taskid -> [ bleuscore, zscorefrom user, recordscore from annoation]
sid2tid2score = defaultdict(lambda:defaultdict(lambda:[0.0,0.0,0.0]))

validusers = set()
for l in open('validusers.txt'):
    validusers.add(l.strip())


alltopicid = set()

for l in open(filename):
    segs = l.strip().split(',')
    sid = segs[0]
    tid = segs[1]
    alltopicid.add(tid)
    score = float(segs[2])
    if sid in validusers:
        sid2tid2score[sid][tid][0]=score

for l in open('recordscore.txt').readlines()[1:]:
    segs = l.strip().split('\t')
    sid = segs[0]
    tid = segs[1]
    zscore = float(segs[3])
    annoscore = float(segs[4])
    if sid in validusers:
        sid2tid2score[sid][tid][1]=zscore
        sid2tid2score[sid][tid][2]=annoscore


# l0 means bleuscore
# l1 means zscore
# l2 means annoscore

result = open('manual.csv','a')

filename = filename.replace('./bleu/','').replace('.bleu','')
gset,oset = filename.split('+')
for tid in alltopicid:
    l0 = list()
    l1 = list()
    l2 = list()

    print tid
    for sid in sid2tid2score.keys():
        l0.append(sid2tid2score[sid][tid][0])
        l1.append(sid2tid2score[sid][tid][1])
        l2.append(sid2tid2score[sid][tid][2])
    from scipy.stats.stats import pearsonr
    from scipy.stats.stats import kendalltau
    print len(l0)
    print 'logBLEU v.s. Satisfaction(self)'
    print pearsonr(l0,l1)[0],pearsonr(l0,l1)[1]
    print 'logBLEU v.s. Record(3rd party)'
    print pearsonr(l0,l2)[0],pearsonr(l0,l2)[1]
    print 'Satisfaction(self) v.s. Record(3rd party)'
    print pearsonr(l1,l2)[0],pearsonr(l1,l2)[1]

    result.write(','.join([str(item) for item in [gset,oset,tid,pearsonr(l0,l2)[0],pearsonr(l0,l2)[1],pearsonr(l0,l1)[0],pearsonr(l0,l1)[1], pearsonr(l1,l2)[0],pearsonr(l1,l2)[1]]])+'\n')
result.close()