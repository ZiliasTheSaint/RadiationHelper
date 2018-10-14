package gui.resources;

import java.util.ListResourceBundle;

/**
 * The resource bundle for Bang class. <br>
 * 
 * @author Dan Fulea, 18 Jun. 2011
 * 
 */
public class BangResources extends ListResourceBundle {
	/**
	 * Returns the array of strings in the resource bundle.
	 * 
	 * @return the resources.
	 */
	public Object[][] getContents() {
		// TODO Auto-generated method stub
		return CONTENTS;
	}

	/** The resources to be localised. */
	private static final Object[][] CONTENTS = {

			// displayed images..
			{ "form.icon.url", "/danfulea/resources/personal.png" },///jdf/resources/duke.png" },
			{ "icon.url", "/danfulea/resources/personal.png" },///jdf/resources/globe.gif" },

			{ "img.zoom.in", "/danfulea/resources/zoom_in.png" },
			{ "img.zoom.out", "/danfulea/resources/zoom_out.png" },
			{ "img.pan.left", "/danfulea/resources/arrow_left.png" },
			{ "img.pan.up", "/danfulea/resources/arrow_up.png" },
			{ "img.pan.down", "/danfulea/resources/arrow_down.png" },
			{ "img.pan.right", "/danfulea/resources/arrow_right.png" },
			{ "img.pan.refresh", "/danfulea/resources/arrow_refresh.png" },

			{ "img.accept", "/danfulea/resources/accept.png" },
			{ "img.insert", "/danfulea/resources/add.png" },
			{ "img.delete", "/danfulea/resources/delete.png" },
			{ "img.delete.all", "/danfulea/resources/bin_empty.png" },
			{ "img.view", "/danfulea/resources/eye.png" },
			{ "img.set", "/danfulea/resources/cog.png" },
			{ "img.report", "/danfulea/resources/document_prepare.png" },
			{ "img.today", "/danfulea/resources/time.png" },
			{ "img.open.file", "/danfulea/resources/open_folder.png" },
			{ "img.open.database", "/danfulea/resources/database_connect.png" },
			{ "img.save.database", "/danfulea/resources/database_save.png" },
			{ "img.substract.bkg", "/danfulea/resources/database_go.png" },
			{ "img.close", "/danfulea/resources/cross.png" },
			{ "img.about", "/danfulea/resources/information.png" },
			{ "img.printer", "/danfulea/resources/printer.png" },
			
			{ "Application.NAME", "BANG - Beta, Alpha, Nuclides and Gamma annalysis" },
			{ "About.NAME", "About" },
			{ "ALPHA.STABILITY.NAME", "Alpha detector long time stability" },
			{ "BETA.STABILITY.NAME", "Beta detector long time stability" },
			{ "GAMMA.STABILITY.NAME", "Gamma detector long time stability" },
			{ "SelfAbsorption.NAME", "Self-Absorption study" },
			{ "KCl.NAME", "KCl calculator" },

			{ "Author", "Author:" },
			{ "Author.name", "Dan Fulea , fulea.dan@gmail.com" },

			{ "Version", "Version:" },
			{ "Version.name", "RadiationHelper Collection 2.5" },

			{
					"License",
					//"This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License (version 2) as published by the Free Software Foundation. \n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. \n" },
			"Copyright (c) 2014, Dan Fulea \nAll rights reserved.\n\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n\n1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n\n2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\nTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" },

			{ "menu.file", "File" },
			{ "menu.file.mnemonic", new Character('F') },

			{ "menu.file.stability.alpha", "Alpha device long time stability" },
			{ "menu.file.stability.alpha.mnemonic", new Character('A') },
			{ "menu.file.stability.alpha.toolTip", "Long time stability for alpha detector" },

			{ "menu.file.stability.beta", "Beta device long time stability" },
			{ "menu.file.stability.beta.mnemonic", new Character('B') },
			{ "menu.file.stability.beta.toolTip", "Long time stability for beta detector" },

			{ "menu.file.stability.gamma", "Gamma device long time stability" },
			{ "menu.file.stability.gamma.mnemonic", new Character('G') },
			{ "menu.file.stability.gamma.toolTip", "Long time stability for gamma detector" },
			
			{ "menu.file.self", "Self absorption study" },
			{ "menu.file.self.mnemonic", new Character('S') },
			{ "menu.file.self.toolTip", "Self absorption study" },

			{ "menu.file.exit", "Close" },
			{ "menu.file.exit.mnemonic", new Character('C') },
			{ "menu.file.exit.toolTip", "Close the application" },

			{ "menu.help", "Help" },
			{ "menu.help.mnemonic", new Character('H') },

			{ "menu.help.about", "About..." },
			{ "menu.help.about.mnemonic", new Character('A') },
			{ "menu.help.about.toolTip",
					"Several informations about this application" },

			{ "menu.help.LF", "Look and feel..." },
			{ "menu.help.LF.mnemonic", new Character('L') },
			{ "menu.help.LF.toolTip", "Change application look and feel" },

			{ "menu.help.kcl", "KCl calculator..." },
			{ "menu.help.kcl.mnemonic", new Character('K') },
			{ "menu.help.kcl.toolTip", "Perform KCl calculations" },
			
			{ "dialog.exit.title", "Confirm..." },
			{ "dialog.exit.message", "Are you sure?" },
			{ "dialog.exit.buttons", new Object[] { "Yes", "No" } },

			//{ "library.inuse.border", "In-use library" },
			{ "gammaAnalysis.button", "Gamma analysis..." },
			{ "gammaAnalysis.button.mnemonic", new Character('G') },
			{ "gammaAnalysis.button.toolTip", "Experimental gamma analysis module." },
			{ "alphaAnalysis.button", "Alpha analysis..." },
			{ "alphaAnalysis.button.mnemonic", new Character('l') },
			{ "alphaAnalysis.button.toolTip", "Experimental alpha analysis module." },
			{ "betaAnalysis.button", "Beta analysis..." },
			{ "betaAnalysis.button.mnemonic", new Character('e') },
			{ "betaAnalysis.button.toolTip", "Experimental beta analysis module." },
			
			{ "neds.button", "Nuclide exposure..." },
			{ "neds.button.mnemonic", new Character('N') },
			{ "neds.button.toolTip", "Theoretical nuclide decay, exposure, dosimetry and shielding." },
			
			{ "alphaMC.button", "Alpha_MC..." },
			{ "alphaMC.button.mnemonic", new Character('A') },
			{ "alphaMC.button.toolTip", "Theoretical Monte-Carlo simulation for alpha detectors." },
			
			{ "gesMC.button", "Beta_Gamma_MC..." },
			{ "gesMC.button.mnemonic", new Character('B') },
			{ "gesMC.button.toolTip", "Theoretical Monte Carlo simulation for beta and gamma detectors." },
			
			{ "theor.border", "Theoretical-based applications" },
			{ "experimental.border", "Experimental-based applications" },
			
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			
			{ "number.error", "Insert valid positive numbers! " },
			{ "number.error.title", "Unexpected error" },
			//=========STABILITY==========
			{ "library.master.db", "ICRP38" },
			{ "library.master.db.indexTable", "ICRP38Index" },
			
			{ "library.master.jaeri.db", "JAERI03" },
			{ "library.master.jaeri.db.indexTable", "JAERI03index" },
						
			{ "stability.db", "Devices" },
			{ "stability.table.alpha", "alphastabilitytable" },
			{ "stability.table.beta", "betastabilitytable" },
			{ "stability.table.gamma", "gammastabilitytable" },
			
			{ "deadtime.table.alpha", "alphadeadtimetable" },
			{ "deadtime.table.beta", "betadeadtimetable" },
			
			{ "setAsReferenceCh", "Set as reference" },
			{ "library.master.dbCb", new String[] { "ICRP38", "JAERI03" } },
			
			{ "stability.add.button", "Add" },
			{ "stability.add.button.mnemonic", new Character('A') },
			{ "stability.add.button.toolTip", "Add data" },
			
			{ "stability.delete.button", "Delete" },
			{ "stability.delete.button.mnemonic", new Character('D') },
			{ "stability.delete.button.toolTip", "Delete data" },

			{ "stability.today.button", "Today" },
			{ "stability.today.button.mnemonic", new Character('T') },
			{ "stability.today.button.toolTip", "Set date as today" },
			
			{ "chart.cps.NAME", "value" },
			{ "chart.cps.unc.NAME", "value" },//series main value!
			{ "chart.x.date", "Days" },
			{ "chart.y.cps", "CPS" },
			{ "chart.y.proc", "%" },
			{ "chart.NAME", "CPS long time stability" },
			{ "uncchart.NAME", "UNC.[%] long time stability" },
			
			{ "stability.nuclide.database", "Nuclide database: " },
			{ "stability.nuclide", "Nuclide: " },
			{ "stability.ug", "Fixed maximum unc. (Ug in %): " },
			{ "reference.border", "Reference data " },
			{ "date.border", "Measurement date" },

			{ "toolbar.day", "Day: " },
			{ "toolbar.month", "Month: " },
			{ "toolbar.year", "Year: " },
			{ "stability.value.tooltip", "Measured CPS value (experimental value, EV or theoretical value, TV, if desired or if it is first record!) " },
			
			{ "stability.value", "Counts per seconds [CPS]: " },
			{ "stability.value.unc", "CPS unc: " },
			
			{ "chart.tvseries.NAME", "TV" },
			{ "chart.evseries.NAME", "EV" },
			{ "chart.tvmugseries.NAME", "TV-Ug" },
			{ "chart.tvpugseries.NAME", "TV+Ug" },
			{ "chart.evmsumsseries.NAME", "EV-SUMS" },
			{ "chart.evpsumsseries.NAME", "EV+SUMS" },
			
			{ "chart.uncseries.NAME", "UNC" },
			{ "chart.pugseries.NAME", "+Ug" },
			{ "chart.mugseries.NAME", "-Ug" },
			{ "chart.psumsseries.NAME", "+SUMS" },
			{ "chart.msumsseries.NAME", "-SUMS" },

			//====================
			{ "kcl.border", "KCl" },
			{ "kcl.k", "Natural K Atomic Mass: " },
			{ "kcl.cl", "Cl Atomic Mass: " },
			{ "kcl.40k", "40K abundance versus natural K [%]: " },
			{ "kcl.initiate.button", "Initialize" },
			{ "kcl.initiate.button.mnemonic", new Character('I') },
			{ "kcl.initiate.button.toolTip", "Initialize mass of radionuclide!" },
			
			{ "kcl.1g", "1 g substance contains (grams of radioactive isotope): " },
			{ "kcl.g", "Grams substance: " },
			{ "kcl.am", "Atomic mass of radioactive isotope: " },
			{ "kcl.hl", "Half life of radioactive isotope [years]: " },
			{ "kcl.a", "Activity [Bq]: " },
			
			{ "kcl.compute.button", "Compute" },
			{ "kcl.compute.button.mnemonic", new Character('C') },
			{ "kcl.compute.button.toolTip", "Compute the radionuclide activity!" },
			
			{ "self.chart.NAME", "Self-Absorption study" },
			{ "self.chart.x", "Standard source mass [arbitrary units]" },
			{ "self.chart.y", "Detection efficiency [arbitrary units]" },
			{ "self.massTf", "Standard source mass: " },
			{ "self.effTf", "Efficiency: " },
			
			{ "self.optimum.massTf", "Optimum standard source mass: " },
			{ "self.optimum.effTf", "Optimum estimated efficiency: " },

			{ "self.add.button", "Add" },
			{ "self.add.button.mnemonic", new Character('A') },
			{ "self.add.button.toolTip", "Add data!" },

			{ "self.delete.button", "Delete" },
			{ "self.delete.button.mnemonic", new Character('D') },
			{ "self.delete.button.toolTip", "Delete data!" },

			{ "self.compute.button", "Compute" },
			{ "self.compute.button.mnemonic", new Character('C') },
			{ "self.compute.button.toolTip", "Compute the optimum mass-efficiency!" },
			
			{ "self.list.nrcrt", "#" },
			{ "self.list.mass", "Mass [arbitrary units]: " },
			{ "self.list.eff", "Eff [arbitrary units]: " },
			
			{ "data.error", "Please add mass-efficiency data first! " },
			{ "data.error.title", "Unexpected error" },
			
			{ "chart.self.data.Name", "Raw data" },
			{ "chart.self.fit.ascend.Name", "Fit data 1" },
			{ "chart.self.fit.lin.Name", "Fit data 2" },
	};



}
