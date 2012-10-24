package com.friendstivals.utils;

import java.util.ArrayList;

public class Festival {
	private String name;
	private String date;
	private String start;
	private String end;
	private String location;
	private ArrayList<Entradas> entradas;
}

class Entradas{
	private String type;
	private String value;
	private ArrayList<String> places;
}