 	var identifiersHidden = false;
	
 	function toggleIdentifiers(name) {
        var rows = document.getElementsByTagName(name);
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			if (row.className == "identifier") {
				if (!identifiersHidden) {
					row.display = row.style.display;
					row.style.display = "none";
				} else {
					row.style.display = row.display;
				}
			}
		}
		var toggle = document.getElementById("toggleIdentifiers");
		toggle.innerHTML = (!identifiersHidden) ? "show identifiers" : "hide identifiers";
		identifiersHidden = !identifiersHidden;
	}
	
 	var constantsHidden = false;
	
 	function toggleConstants(name) {
        var rows = document.getElementsByTagName(name);
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			if (row.className == "constant") {
				if (!constantsHidden) {
					row.display = row.style.display;
					row.style.display = "none";
				} else {
					row.style.display = row.display;
				}
			}
		}
		var toggle = document.getElementById("toggleConstants");
		toggle.innerHTML = (!constantsHidden) ? "show constants" : "hide constants";
		constantsHidden = !constantsHidden;
	}

 	var sparklinesHidden = false;
	
 	function toggleSparklines(name) {
        var rows = document.getElementsByTagName(name);
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			if (row.className == "sparkline") {
				if (!sparklinesHidden) {
					row.display = row.style.display;
					row.style.display = "none";
				} else {
					row.style.display = row.display;
				}
			}
		}
		var toggle = document.getElementById("toggleSparklines");
		toggle.innerHTML = (!sparklinesHidden) ? "show sparklines" : "hide sparklines";
		sparklinesHidden = !sparklinesHidden;
	}
	