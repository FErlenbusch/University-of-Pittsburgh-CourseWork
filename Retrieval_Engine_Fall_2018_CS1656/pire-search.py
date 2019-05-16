import os
import sys
import json
from nltk import regexp_tokenize, PorterStemmer
from math import log2
from collections import OrderedDict


def format_word(word):
    stemmer = PorterStemmer()
    return stemmer.stem(word.replace('\'', ''))


def get_document_list(index, keywords):
    docs = []
    
    for keyword in keywords:
        word = format_word(keyword)
        
        if word in index['words']:
            for doc in index['words'][word]['docs']:
                if doc not in docs:
                    docs.append(doc)

    return docs


def calc_score(index, word, doc):
    w = index['words'][word]['docs'][doc]
    N = index['doc_count']
    n = index['words'][word]['doc_count']
    
    return (1.0 + log2(w)) * log2(N / n)


def get_score(index, keywords, doc):
    score = {'total': 0.0}
    
    for keyword in keywords:
        word = format_word(keyword)
        
        if word in index['words'] and doc in index['words'][word]['docs']:
            score[keyword] = calc_score(index, word, doc)
        else:
            score[keyword] = 0.0
            
        score['total'] += score[keyword]
        
    return score
     

def print_scores(out_file, scores, keywords):
    count = 0
    last_value = sys.float_info.max
    
    ordered = OrderedDict(sorted(scores.items(), key = lambda x: x[1]['total'], reverse = True))
    
    for score in ordered:
        if ordered[score]['total'] < last_value:
            last_value = ordered[score]['total'] 
            count += 1;
        
        out_file.write('[' + str(count) + '] file=' + score + ' score=' + 
              "{:.6f}".format(ordered[score]['total']) + '\n')
        
        for word in keywords:
            out_file.write('    weight(' + word + ')=' + "{:.6f}".format(ordered[score][word]) + '\n')
        
        out_file.write('\n')
        

def get_keywords(out_file, index, line):
    out_file.write('------------------------------------------------------------\n')
    out_file.write('keywords = ' + line.lower() + '\n')
    
    keywords = regexp_tokenize(line.lower(), r'[\s]\s*', gaps = True)
    
    docs = get_document_list(index, keywords)
    
    scores = {}
    
    for doc in docs:
        scores[doc] = get_score(index, keywords, doc)
         
    print_scores(out_file, scores, keywords)
    
    

with open('inverted-index.json') as index_file:
    index = json.load(index_file)
    
out_file = open('output.txt', 'w')
out_file.write('Information Retrieval Engine - Frederick Erlenbusch (fwe4@pitt.edu)\n\n')
    
with open('keywords.txt', 'r') as keyword_file:
    line = keyword_file.readline()
    
    while line:
        get_keywords(out_file, index, line)
        line = keyword_file.readline()
        