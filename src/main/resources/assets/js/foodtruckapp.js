"use strict";

window.APP = window.APP || {};

APP.map = {};

APP.FoodTruckModel = Backbone.Model.extend({
    urlRoot: "/foodtruck",
    defaults: {
        id: "",
        applicant: "",
        fooditems: "",
        longitude: "",
        latitude: "",
        locationdescription: "",
        schedule: "",
        address: "",
        x: "",
        y: "",
        permit: "",
        block: "",
        blocklot: ""
    }
});

APP.FoodTruckCollection = Backbone.Collection.extend({
    model: APP.FoodTruckModel,
    url: "/foodtrucks"
});

APP.FoodTruckRouter = Backbone.Router.extend({
    routes: {
        "foodtrucks/index": "index",
        "foodtruck/:id/view": "show"
    },

    initialize: function (options) {
        this.foodtrucks = options.foodtrucks;

        this.index();
        this.createForm();
    },

    show: function (id) {
        var foodtruck = this.foodtrucks.get(id);
        this.currentView = new APP.FoodTruckShowView({
            foodtruck: foodtruck
        });
        $('#primary-content').html(this.currentView.render().el);
    },

    index: function () {
        this.currentView = new APP.FoodTruckIndexView({
            foodtrucks: this.foodtrucks
        });
        $('#primary-content').html(this.currentView.render().el);
    },

    createForm: function () {
        this.formView = new APP.FoodTruckFormView({
            foodtrucks: this.foodtrucks
        });
        $('#inputForm').html(this.formView.render().el);
    }
});

APP.FoodTruckFormView = Backbone.View.extend({

    initialize: function (options) {
        this.foodtrucks = options.foodtrucks;
        this.keyword = "";
        this.latitude = 0;
        this.longitude = 0;
    },

    // populate the html to the dom
    render: function () {
        this.$el.html($('#formTemplate').html());
        return this;
    },

    events: {
        "click #search": "search"
    },

    search: function (event) {
        event.preventDefault();
        event.stopPropagation();

        this.keyword = $('#keyword').val();
        this.latitude = $('#latitude').val();
        this.longitude = $('#longitude').val();

        console.log(this.keyword + ", " + this.latitude + ", " + this.longitude);

        this.foodtrucks.fetch({
                data: {
                    keyword: this.keyword,
                    latitude: this.latitude,
                    longitude: this.longitude
                },
                reset: true}
        )
    }

});

APP.FoodTruckIndexView = Backbone.View.extend({

    initialize: function (options) {
        this.mapMarkers = [];

        this.foodtrucks = options.foodtrucks;
        this.foodtrucks.bind('reset', this.addAll, this);
    },

    // populate the html to the dom
    render: function () {
        this.$el.html($('#indexTemplate').html());
        this.addAll();
        return this;
    },

    addAll: function () {
        // clear out the container each time you render index
        this.$el.find('tbody').children().remove();
        this.deleteMarkers();

        _.each(this.foodtrucks.models, $.proxy(this, 'addOne'));
        _.each(this.foodtrucks.models, $.proxy(this, 'addMarker'));

        this.setAllMap(APP.map);
    },

    addOne: function (foodtruck) {
        var view = new APP.FoodTruckRowView({
            foodtrucks: this.foodtrucks,
            foodtruck: foodtruck
        });

        this.$el.find("tbody").append(view.render().el);
    },

    addMarker: function(foodtruck) {

        var truckName = foodtruck.get('applicant');
        var latitude = foodtruck.get('latitude');
        var longitude = foodtruck.get('longitude');
        var schedule = foodtruck.get('schedule');

        var marketPosition = new google.maps.LatLng(latitude, longitude);
        var marker = new google.maps.Marker({
            position: marketPosition,
            map: APP.map,
            title: truckName,
            icon: 'img/truck3.png'
        });

        var contentString =
            '<div id="content">' +
            '  <h4>' + truckName + '</h4>' +
            '  <div id="bodyContent">' +
            '  <p><a href="' + schedule + '">' +
            '  View Schedule (PDF)</a></p>' +
            '  </div>' +
            '</div>';

        var infowindow = new google.maps.InfoWindow({
            content: contentString
        });

        google.maps.event.addListener(marker, 'click', function() {
            infowindow.open(APP.map, marker);
        });

        this.mapMarkers.push(marker);
    },

    setAllMap: function(map) {
        for (var i = 0; i < this.mapMarkers.length; i++) {
            this.mapMarkers[i].setMap(map);
        }
    },

    deleteMarkers: function() {
        this.setAllMap(null);
        this.mapMarkers = [];
    }

});

APP.FoodTruckRowView = Backbone.View.extend({
    // the wrapper defaults to div, so only need to set this if you want something else
    // like in this case we are in a table so a tr
    tagName: "tr",

    // the constructor
    initialize: function (options) {
        this.foodtruck  = options.foodtruck;
        this.foodtrucks = options.foodtrucks;
    },

    // populate the html to the dom
    render: function () {
        this.$el.html(_.template($('#rowTemplate').html(), this.foodtruck.toJSON()));
        return this;
    }
});

APP.FoodTruckShowView = Backbone.View.extend({
    initialize: function (options) {
        this.foodtruck = options.foodtruck;
    },

    render: function () {
        this.$el.html(_.template($('#showTemplate').html(), this.foodtruck.toJSON()));
        return this;
    }
});

/*
 * Auto-complete for geo location
 */
APP.createLocationLookup = function() {
    $("#geocomplete").geocomplete({
        map: ".map_canvas",
        details: "form",
        types: ["geocode", "establishment"]
    });

    $("#find").click(function(){
        $("#geocomplete").trigger("geocode");
    });

    APP.map = $("#geocomplete").geocomplete('map');

    var marketPosition = new google.maps.LatLng(latitude, longitude);
}

/*
 * Auto-complete for keywords
 */
APP.createKeywordLookup = function() {

    var foodItems = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        prefetch: {
            url: '/autosuggestwords',
            filter: function (list) {
                return $.map(list, function (keyword) {
                    return { name: keyword };
                });
            }
        }
    });

    var foodTruckNames = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        prefetch: {
            url: '/trucknames',
            filter: function (list) {
                return $.map(list, function (keyword) {
                    return { name: keyword };
                });
            }
        }
    });

    foodItems.initialize();
    foodTruckNames.initialize();

    $('#keyword').typeahead(
        {
            hint: true,
            highlight: true,
            minLength: 1
        },
        {
            name: 'food-items',
            displayKey: 'name',
            source: foodItems.ttAdapter(),
            templates: {
                header: '<h3 class="keyword-name">Food Items</h4>'
            }
        },
        {
            name: 'truck-names',
            displayKey: 'name',
            source: foodTruckNames.ttAdapter(),
            templates: {
                header: '<h3 class="keyword-name">Truck Names</h4>'
            }
        }
    );
}