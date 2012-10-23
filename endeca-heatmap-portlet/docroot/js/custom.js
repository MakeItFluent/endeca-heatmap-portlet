var map, mapDivID;
function createGraph(data, controller) {
	var heatmapData = [];
	var bounds = new google.maps.LatLngBounds;
	
	// load the names of the fields to use from the edit model
	var latLongField = controller.viewModel.editModel.locationField;
	var weightField = controller.viewModel.editModel.weightField;
	// load whether to use the weight field from the edit model 
	var useWeightField = controller.viewModel.editModel.useWeightField;
	
	// get the map object from the view model
	var map = controller.viewModel.map;
	
	for(var i=0; i < data.length; i++) {
		// null checks
		if(typeof(data[i][latLongField]) != 'undefined' && 
			(!useWeightField || typeof(data[i][weightField]) != 'undefined')) {
			
			// the field has the lattitude and longitude space separated
			var latlng = data[i][latLongField].split(' ');
			if(latlng.length = 2) {
				
				var lng = latlng[0];
				var lat = latlng[1];
				var loc = new google.maps.LatLng(lat, lng);
				
				// add heatmap data for this point to the array, with or without the optional weight field
				if(useWeightField) {
					var price = parseInt(data[i][weightField], 10);
					heatmapData.push({location: loc, weight: price});
				}
				else {
					heatmapData.push(loc);
				}
				
				// include this point in the bounds
				bounds.extend(loc);
			}
		}
	}
	
	// create the heatmap using data in heatmapData
	var heatmap = new google.maps.visualization.HeatmapLayer({
		data: heatmapData
	});
	
	// add heatmap to the map
	heatmap.setMap(map);
	
	// zoom to show all of the heatmap
	map.fitBounds(bounds);
}

function createMap(mapDivID, controller) {
	// create the map
	var cambsEly = new google.maps.LatLng(52.2276, 0.143);
	
	// store a reference on the controller object
	controller.viewModel.map = new google.maps.Map(
			document.getElementById(mapDivID), {
   	  center: cambsEly,
   	  zoom: 9,
   	  mapTypeId: google.maps.MapTypeId.TERRAIN
   	});
}