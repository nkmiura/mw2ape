import nltk

print("Hello world!")

# Ler arquivo de definicao da gramatica carregando na estrutura de dados inicial
# Tratamento para as expressoes iniciais - rotulos iniciais
# Substituicoes
# Tratamento de recursao

rule_raw = []

grammar_filename = 'Dados/exemplo1.txt'

with open(grammar_filename) as gf:
    rule_raw = gf.readlines()

index = 1

for line in rule_raw:
    rule = []
    rule.append(index)
    rule.append(nltk.word_tokenize(line))
    print(str(rule))
    index = index + 1
