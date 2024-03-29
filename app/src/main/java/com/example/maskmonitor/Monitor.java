package com.example.maskmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Objects;

public class Monitor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        final String host = "834189a683af4a7ba798b512a1efaa04.s1.eu.hivemq.cloud";
        final String username = "mobilApp";
        final String password = "AppMob2020";


        //create an MQTT client
        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildBlocking();

        //connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        //subscribe to the topic "agri/info"
        client.subscribeWith()
                .topicFilter("testTopic/m")
                .send();

        TextView pump = (TextView) findViewById(R.id.pump);
        TextView ne = (TextView)findViewById(R.id.ne);
        TextView so = (TextView)findViewById(R.id.so);

        //set a callback that is called when a message is received (using the async API style)
        client.toAsync().publishes(ALL, publish -> {
            System.out.println("Received message: " + publish.getTopic() + " -> " + UTF_8.decode(publish.getPayload().get()));
            String ch = (String)String.valueOf(UTF_8.decode(publish.getPayload().get()));
            String res = ch.substring(1);
            char c = ch.charAt(0);
            System.out.println(res) ;
        if(c=='N')
            {
                ne.setText(res);

            }else if(c=='P')
            {
                pump.setText(res);
            }else{
                so.setText(res);

            }

        });


        ImageView back = (ImageView) findViewById(R.id.next);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Monitor.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}