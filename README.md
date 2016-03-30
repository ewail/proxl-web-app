#ProXL Web Application
ProXL is a web application for analyzing, visualizing, and sharing protein cross-linking mass spectrometry data. It has been designed for maximum flexibility--it supports data generated by any pipeline or any FASTA protein naming database. 

##Documentation
To learn more about what ProXL is, please see our paper at (link to eventual publication).

For comprehensive documentation describing ProXL's features and how to use or install ProXL, please see our documentation here: http://proxl-web-app.readthedocs.org/en/latest/

##Demo Site
We have set up a demo website to demonstrate many of ProXL's features. Please visit our demo site here: http://yeastrc.org/proxl_demo/

##ProXL XML and Importing Data
To simplify support for as many software pipelines as possible, we have developed a XML specification dubbed ProXL XML for describing the cross-linking proteomics results from any software pipeline. Data imported into ProXL must first be converted to ProXL XML. To learn more about ProXL XML and the import process, please visit our [import documentation](http://proxl-web-app.readthedocs.org/en/latest/install/import.html).

Converters for several major protein cross-linking mass spectrometry pipelines have already been developed. For your convenience, links to the respective importers are provided below. Please be sure to review the [import documentation](http://proxl-web-app.readthedocs.org/en/latest/install/import.html) for more information.

    * [Kojak](https://github.com/yeastrc/proxl-import-kojak)
    * [Crux] (https://github.com/yeastrc/proxl-import-crux)
    * [pLink](https://github.com/yeastrc/proxl-import-plink)
    * [StavroX](https://github.com/yeastrc/proxl-import-stavrox)
    * [xQuest](https://github.com/yeastrc/proxl-import-xquest)

Please send any questions, suggestions, or any other feedback to proxl-help@yeastrc.org.
