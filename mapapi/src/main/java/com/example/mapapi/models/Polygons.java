package com.example.mapapi.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Polygons extends ArrayList<Polygon> {

}
