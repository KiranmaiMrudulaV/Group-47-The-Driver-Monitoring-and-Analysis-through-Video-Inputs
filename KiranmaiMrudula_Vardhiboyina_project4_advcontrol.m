%function[outputswitch] = KiranmaiMrudula_Vardhiboyina_project4_advcontrol(drowsy,distracted,road_conditons,hr,rr)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%input
drowsy=1;%values range[0 1]
distracted=0.9;%values range[0 1]
road_conditions=0;%values range[0 1]
hr=80;
rr=6;
tr=0.01*(hr/rr);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Gain = 100000; %default gain value
initSpeed=40;% init speed default value

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



decelLim=(-1)*( 200 * road_conditions + 150 * (1-road_conditions));%setting decel limit

human_decel_lim=1.1*decelLim;%human deceleration limit


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%vehicle running in auto mode.........

[A,B,C,D,Kess, Kr, Ke, uD] = designControl(secureRand(),Gain);
    
    open_system('Autonomous.slx')
    set_param('Autonomous/VehicleKinematics/Saturation','LowerLimit',num2str(decelLim))
    set_param('Autonomous/VehicleKinematics/vx','InitialCondition',num2str(initSpeed))
    simModel = sim('Autonomous.slx');

    time = simModel.sx1.Time;
    sx1_data = simModel.sx1.Data;
    vx1_data = simModel.vx1.Data;
    ax1_data = simModel.ax1.Data;


if sx1_data(end)>=0% if collision
    
    %calculate_overall_tr
    atr=Additional_TR(drowsy,distracted);
    overall_tr=tr+atr;

    %calculate fuzzy logic whether to switch or not
    switch_or_not = Switching(drowsy,distracted,road_conditions);


    if switch_or_not==0 %level_of_danger is not high and can be switched


        [A,B,C,D,Kess, Kr, Ke, uD] = designControl(secureRand(),Gain);
        
        open_system('HumanActionModel.slx')
        set_param('HumanActionModel/VehicleKinematics/Saturation','LowerLimit',num2str(human_decel_lim))
        set_param('HumanActionModel/VehicleKinematics/vx','InitialCondition',num2str(initSpeed))  
        simModel = sim('HumanActionModel.slx');
    
        htime = simModel.sx1.Time;
        hsx1_data = simModel.sx1.Data;
        hvx1_data = simModel.vx1.Data;
        hax1_data = simModel.ax1.Data;


        if hsx1_data(end)>=0% collision happened even though switched to human
            
            fprintf( "collision is inevitable even though switched to human" );

            outputswitch=0;
    
        else %collison not happened
            
            outputswitch=1;
            fprintf( "switch to human" );
        end  


    else

        %level of danger is high so dont switch

            outputswitch=2;
            fprintf( "collision is inevitable because level of danger is high" );
            


    end

else

    outputswitch=3;
    fprintf( "No collision" );
end











