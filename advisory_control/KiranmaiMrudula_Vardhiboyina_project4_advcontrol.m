%  Inputs from firebase real-time database 
firebaseUrl_ = 'https://cse535project4ui-default-rtdb.firebaseio.com/172cdea9-1774-4c03-8404-b3b05183a3b7.json';

record_json = webread(firebaseUrl_,optionsGet);

drowsy = record_json.drowsinessProbability;
distracted= record_json.distractionProbability;
road_conditions=record_json.workloadProbability;
hr=record_json.hr;
rr=record_json.rr;



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Gain = 100000; %default gain value
initSpeed=40;% init speed default value

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

tr=0.01*(hr/rr);%calculating reaction time

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

            outputswitch='collision is inevitable even though switched to human';
    
        else %collison not happened
            
            outputswitch='switch to human';
            fprintf( "switch to human" );
        end  


    else

        %level of danger is high so dont switch

            outputswitch='collision is inevitable because level of danger is high';
            fprintf( "collision is inevitable because level of danger is high" );
            


    end

else

    outputswitch='No collision';
    fprintf( "No collision" );
end


% sending output back to firebase and android

firebaseURL_output = 'https://cse535project4ui-default-rtdb.firebaseio.com/Matlab_Results.json';

% Commenting the output string to of having switch for now for testing. This will be coming from fuzzy logic controller.

data = struct('Advisory_Control_Decision', outputswitch); 

options = weboptions('MediaType', 'application/json', 'RequestMethod', 'put');

try
    response_json = webwrite(firebaseURL_output, data, options);
    disp('Data successfully sent to firebase.');
    disp(response_json); 
catch e
    disp('Error sending data');

    disp(getReport(e));
end



