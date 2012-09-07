Use SOLR to allow clinicians to easily and efficiently search the patient's chart - TRUNK-3639

Usage:
1) Install Module
2) Go to Administration -> Under "Solr Module" click "Import Data"
3) Still Under Administration -> Under "Solr Module" click "Data Import Status"
Depending on how big your data is, this may take from 1 minute and more. 
For instance the default 5k patient demo data takes around 10 minutes on my laptop.
So after a couple of minutes (depending on the size of your data), refresh the
"Data Import Status" page until when the status changes from "Busy" to "Idle". 
After which you will search for a patient and then go to the "Chart Search" tab on
on the patient dashboard, where you can type some text for a concept name, description, or value_text 
to get some results. Clicking on any result link should take you to the obs page.

NOTE: I did most of my testing on OpenMRS 1.9 and above
