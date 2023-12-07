function[addTR] = Additional_TR(drowsy,distracted)

fis = readfis('additionalTRFuzzylogic.fis');
fisInput = [drowsy,distracted];
addTR = evalfis(fis, fisInput);