/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jetsetter.hoteldashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.jetsetter.hoteldashboard.model.*;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.ws.rs.core.MultivaluedMap;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gim
 */
public class AgentBackOffice extends javax.swing.JFrame {

    /**
     * Creates new form HotelView
     */
    public AgentBackOffice() {
        initComponents();
        fillDateCmb();
        loadCountry();
    }

    private String getDataFromStream(ClientResponse clientResponse) {
        Scanner s = new Scanner(clientResponse.getEntityInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void loadCountry() {
        JerseyClient jerseyClient = JerseyClient.getInstance();
        ClientResponse response = jerseyClient.getData("country/", null);
        Gson gson = new Gson();
        if (response != null && response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            Country[] countries = gson.fromJson(getDataFromStream(response), Country[].class);
            List<Country> mcList = Arrays.asList(countries);
            cmbCountryCode.removeAllItems();
            for (Country country : mcList) {
                if (country.getCountryName() != null) {
                    cmbCountryCode.addItem(country.getCountryIso() + "-" + country.getCountryName());
                }
            }
        }
    }

    private void loadCityForCountry() {
        JerseyClient jerseyClient = JerseyClient.getInstance();
        String countryCode = (String) cmbCountryCode.getSelectedItem().toString().split("-")[0];
        MultivaluedMap multiValueMap = new MultivaluedMapImpl();
        multiValueMap.add("countryCode", countryCode);

        ClientResponse resp = jerseyClient.getData("city", multiValueMap);
        Gson gson = new Gson();
        cmbCityCode.removeAllItems();
        if (resp != null && resp.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            // 2. JSON to Java object, read it from a Json String.
            City[] cities = gson.fromJson(getDataFromStream(resp), City[].class);
            List<City> mcList = Arrays.asList(cities);
            for (City city : mcList) {
                cmbCityCode.addItem(city.getZipcode() + "-" + city.getCityName());
            }
        }
    }

    private void fillDateCmb() {
        cmbDDFrom.removeAllItems();
        cmbDDTo.removeAllItems();
        cmbMMFrom.removeAllItems();
        cmbMMTo.removeAllItems();
        cmbYYFrom.removeAllItems();
        cmbYYTo.removeAllItems();
        for (int x = 1; x < 32; x++) {
            cmbDDFrom.addItem(x + "");
            cmbDDTo.addItem(x + "");

        }

        for (int x = 1; x < 13; x++) {
            cmbMMFrom.addItem(x + "");
            cmbMMTo.addItem(x + "");

        }

        for (int x = 2017; x < 2050; x++) {
            cmbYYFrom.addItem(x + "");
            cmbYYTo.addItem(x + "");

        }
    }

    private void loadHotels() {
        JerseyClient jerseyClient = JerseyClient.getInstance();
        String countryCode = null;
        String cityCode = null;
        if (cmbCountryCode.getSelectedItem() != null && cmbCityCode.getSelectedItem() != null) {
            countryCode = (String) cmbCountryCode.getSelectedItem().toString().split("-")[0].trim();
            cityCode = (String) cmbCityCode.getSelectedItem().toString().split("-")[0].trim();
            MultivaluedMap multiValueMap = new MultivaluedMapImpl();
            multiValueMap.add("countryCode", countryCode);
            multiValueMap.add("zipCode", cityCode);

            ClientResponse resp = jerseyClient.getData("hotel", multiValueMap);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();


            cmdHotels.removeAllItems();
            if (resp != null && resp.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
                // 2. JSON to Java object, read it from a Json String.
                Hotel[] hotels = gson.fromJson(getDataFromStream(resp), Hotel[].class);
                List<Hotel> mcList = Arrays.asList(hotels);
                for (Hotel hotel : mcList) {
                    cmdHotels.addItem(hotel.getHotelId());
                }
            }
        }

    }

    private void checkAvilability() throws ParseException, JsonProcessingException {
        JerseyClient jerseyClient = JerseyClient.getInstance();

        String hotelCode = null;
        if (cmbCountryCode.getSelectedItem() != null && cmbCityCode.getSelectedItem() != null && cmdHotels.getSelectedItem() != null) {

            hotelCode = (String) cmdHotels.getSelectedItem().toString();

            List<RoomAvailability> roomAvailabilities = new ArrayList<RoomAvailability>();
            if (txtNoOfSingleRoom.getText().length() > 0) {
                RoomAvailability roomAvailabilitySingle = new RoomAvailability();
                roomAvailabilitySingle.setRoomType(RoomTypeEnum.SINGLE_ROOM.roomId);
                roomAvailabilitySingle.setNoOfRooms(Integer.valueOf(txtNoOfSingleRoom.getText()));
                roomAvailabilities.add(roomAvailabilitySingle);
            }

            if (txtNoOfDpoubleRoom.getText().length() > 0) {
                RoomAvailability roomAvailabilityDouble = new RoomAvailability();
                roomAvailabilityDouble.setRoomType(RoomTypeEnum.DOUBLE_ROOM.roomId);
                roomAvailabilityDouble.setNoOfRooms(Integer.valueOf(txtNoOfDpoubleRoom.getText()));
                roomAvailabilities.add(roomAvailabilityDouble);
            }

            if (txtNoOfTripleRoom.getText().length() > 0) {
                RoomAvailability roomAvailabilityTriple = new RoomAvailability();
                roomAvailabilityTriple.setRoomType(RoomTypeEnum.TRIPLE_ROOM.roomId);
                roomAvailabilityTriple.setNoOfRooms(Integer.valueOf(txtNoOfTripleRoom.getText()));
                roomAvailabilities.add(roomAvailabilityTriple);
            }

            AvailabilityData availabilityData = new AvailabilityData();
            availabilityData.setRoomAvailabilities(roomAvailabilities);
            availabilityData.setHotelCode(hotelCode);
            availabilityData.setFromDate(parseDate(cmbDDFrom.getSelectedItem()+"/"+cmbMMFrom.getSelectedItem()+"/"+cmbYYFrom.getSelectedItem()+""));
            availabilityData.setToDate(parseDate(cmbDDTo.getSelectedItem()+"/"+cmbMMTo.getSelectedItem()+"/"+cmbYYTo.getSelectedItem()+""));

            ObjectMapper mapper = new ObjectMapper();
            //  mapper.writeValue(new File("D:\\Android\\shop.json"), list);
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(availabilityData);
            System.out.println(json);
            //jtxResponse.setText(json);
            String resp =  jerseyClient.postData(json,"reservation/");

           jtxResponse.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp));

        }
    }

    private Date parseDate(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate;

        startDate = df.parse(date);
        return startDate;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbCountryCode = new javax.swing.JComboBox();
        cmbCityCode = new javax.swing.JComboBox();
        cmdHotels = new javax.swing.JComboBox();
        txtNoOfSingleRoom = new javax.swing.JTextField();
        txtNoOfDpoubleRoom = new javax.swing.JTextField();
        txtNoOfTripleRoom = new javax.swing.JTextField();
        cmbDDFrom = new javax.swing.JComboBox();
        cmbMMFrom = new javax.swing.JComboBox();
        cmbYYFrom = new javax.swing.JComboBox();
        cmbDDTo = new javax.swing.JComboBox();
        cmbYYTo = new javax.swing.JComboBox();
        cmbMMTo = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxResponse = new javax.swing.JTextArea();
        cmbSubmit = new javax.swing.JButton();
        cmbClear = new javax.swing.JButton();
        cmbExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("Agent Back Office");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(169, 169, 169))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Country Code");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("City");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Hotel");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("No of Single Room(s)");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("No of Double Room(s)");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("No of Tripple Room(s)");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("From");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("To");

        cmbCountryCode.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbCountryCode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmdCountryCodeItemStateChanged(evt);
            }
        });

        cmbCityCode.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbCityCode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCityCodeItemStateChanged(evt);
            }
        });

        cmdHotels.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmdHotels.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmdHotelsItemStateChanged(evt);
            }
        });
        cmdHotels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdHotelsActionPerformed(evt);
            }
        });

        txtNoOfSingleRoom.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        txtNoOfDpoubleRoom.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        txtNoOfTripleRoom.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        cmbDDFrom.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbDDFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        cmbDDFrom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbDDFromItemStateChanged(evt);
            }
        });

        cmbMMFrom.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbMMFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        cmbYYFrom.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbYYFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        cmbDDTo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbDDTo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        cmbYYTo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbYYTo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        cmbMMTo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cmbMMTo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(159, 159, 159)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(cmbDDFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cmbMMFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cmbYYFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(cmbDDTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(cmbMMTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cmbYYTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(txtNoOfTripleRoom, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(cmdHotels, javax.swing.GroupLayout.Alignment.LEADING, 0, 250, Short.MAX_VALUE)
                                                .addComponent(cmbCityCode, javax.swing.GroupLayout.Alignment.LEADING, 0, 156, Short.MAX_VALUE)
                                                .addComponent(cmbCountryCode, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(txtNoOfSingleRoom, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtNoOfDpoubleRoom, javax.swing.GroupLayout.Alignment.LEADING)))
                                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(cmbCountryCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(cmbCityCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(cmdHotels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(txtNoOfSingleRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(txtNoOfDpoubleRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(txtNoOfTripleRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(cmbDDFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmbMMFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmbYYFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel8)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(cmbDDTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmbMMTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmbYYTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(27, Short.MAX_VALUE))
        );

        jtxResponse.setBackground(new java.awt.Color(0, 0, 0));
        jtxResponse.setColumns(20);
        jtxResponse.setForeground(new java.awt.Color(51, 255, 0));
        jtxResponse.setRows(5);
        jScrollPane1.setViewportView(jtxResponse);

        cmbSubmit.setText("Check ");
        cmbSubmit.setActionCommand("Avilability");
        cmbSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    try {
                        cmbSubmitMouseClicked(evt);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        cmbSubmit.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        cmbClear.setText("Clear");
        cmbClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbClearMouseClicked(evt);
            }
        });

        cmbExit.setText("Exit");
        cmbExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbExitMouseClicked(evt);
            }
        });
        cmbExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(216, 216, 216)
                                .addComponent(cmbSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cmbClear, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cmbExit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cmbClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmbExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmbSubmit))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbExitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbExitActionPerformed

    private void cmdHotelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdHotelsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdHotelsActionPerformed

    private void cmdCountryCodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmdCountryCodeItemStateChanged
        loadCityForCountry();
    }//GEN-LAST:event_cmdCountryCodeItemStateChanged

    private void cmbCityCodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCityCodeItemStateChanged
        loadHotels();
    }//GEN-LAST:event_cmbCityCodeItemStateChanged

    private void cmdHotelsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmdHotelsItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdHotelsItemStateChanged

    private void cmbSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSubmitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSubmitActionPerformed

    private void cmbSubmitMouseClicked(java.awt.event.MouseEvent evt) throws ParseException, JsonProcessingException {//GEN-FIRST:event_cmbSubmitMouseClicked
         checkAvilability();
    }//GEN-LAST:event_cmbSubmitMouseClicked

    private void cmbClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbClearMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClearMouseClicked

    private void cmbExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbExitMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbExitMouseClicked

    private void cmbDDFromItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbDDFromItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDDFromItemStateChanged

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbCityCode;
    private javax.swing.JButton cmbClear;
    private javax.swing.JComboBox cmbDDFrom;
    private javax.swing.JComboBox cmbDDTo;
    private javax.swing.JButton cmbExit;
    private javax.swing.JComboBox cmbMMFrom;
    private javax.swing.JComboBox cmbMMTo;
    private javax.swing.JButton cmbSubmit;
    private javax.swing.JComboBox cmbYYFrom;
    private javax.swing.JComboBox cmbYYTo;
    private javax.swing.JComboBox cmbCountryCode;
    private javax.swing.JComboBox cmdHotels;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jtxResponse;
    private javax.swing.JTextField txtNoOfDpoubleRoom;
    private javax.swing.JTextField txtNoOfSingleRoom;
    private javax.swing.JTextField txtNoOfTripleRoom;
    // End of variables declaration//GEN-END:variables
}
