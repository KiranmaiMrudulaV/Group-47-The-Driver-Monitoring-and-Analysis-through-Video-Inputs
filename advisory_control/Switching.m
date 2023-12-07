function[switch_or_not] = Switching(drowsy,distracted,road_conditions)


fis = readfis('KiranmaiMrudulaVardhiboyinaproject4.fis');
fisInput = [drowsy,road_conditions,distracted];
level_of_danger = evalfis(fis, fisInput);

if level_of_danger<= 0.7 %setting threshhold
    switch_or_not=1;
    %can be switched to human
else
    switch_or_not= 0;
    %cannot be switched to human mode since danger is high
end


