package org.nuxeo.project.sample.complementary;

public class Constant {
	
	public static final String INFO_DASHES = "[INFO] ==================================";
	public static final String ERROR_DASHES = "[ERROR] ==================================";
	public static final String LOG_COMMENT_1 = " >=====";
	public static  String Log_template = "0";
	public static  String access_token="";

     
	 
	public static final String PATH_FILES = "../TemplatesPdf";
	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String ROOT_FOLDER_BC = "/default-domain/workspaces/Espace de travail/Dossier BC";  

	
	public static final String LOG_COMMENT_STATE_DELETED = "' AND ecm:currentLifeCycleState != 'deleted'";
	public static final String LOG_COMMENT_STATE_DELETED1 = " AND ecm:currentLifeCycleState != 'deleted'";

	public static final String FILE_CONTENT = "file:content";

	public static final String DUBLINCORE = "dublincore";
	public static final String TITLE_DUBLINCORE = "title";
	
	public static final String PATH_START_INVOICE = "/api/v1/invoice";
	public static final String PATH_START_Product = "/api/v1/product";
	 
		
		 
	
		public static final String NUM_BC = "num_bc";
		
		public static final String BON_LIVRAISON = "BonsLivraaison";
		public static final String CLIENT_BL = "client";
		public static final String NUM_BL = "num_bl";
		public static final String CODE_CLIENT_BL = "code_client";
		
		public static final String BC = "bc";
		public static final String CODE_CLIENT_BC = "code_client";
		
		public static final String FICHE_CLIENT = "fiche-client";
		public static final String CODE_CLIENT_FC = "code-client";
		public static final String BC_OBLIGATOIRE_FC = "isBCRequired";
		public static final String BL_OBLIGATOIRE_FC = "isBLRequired";
		 
		//ID FICHE CLIENT LOCAL : 'd79a846b-b3ee-4156-9db9-fae96f0c770c'
		//ID FICHE CLIENT DISTA : '2871d4d2-3c0a-4fda-b255-94ed31c13014'
		//public static final String ID_FOLDER_FC = "d79a846b-b3ee-4156-9db9-fae96f0c770c";
		
		//ID INVOICE LOCAL : 'd7c0fa4a-47ba-4f81-b60e-f402c086afb7'
		//ID INVOICE DISTA : 'd7c0fa4a-47ba-4f81-b60e-f402c086afb7' pas encore 
		public static final String ID_FOLDER_INVOICE = "d7c0fa4a-47ba-4f81-b60e-f402c086afb7";
		public static final String NAME_FOLDER_GLOBALE = "Dossier factures globale";
		public static final String TYPE_FOLDER_GLOBALE = "folderInvoice";
		
		//http://localhost:8083/e-Doc360-SG2
		//https://secure.360businessventures.com/e-Doc360-AirLiquide
		//public static final String BASE_URL = "http://localhost:8083/e-Doc360-SG2";
		public static final String PATH_START = "/api/v1/ar";

		
		//************************************ INVOICE CANSTANTS *******************************************
		public static final String Bon_Commande = "bon_commande";
		public static final String Reception = "reception";
		public static final String num_commande = "num_commande";
		
		public static final String FACTURE = "FactureFournisseur";
		public static final String NUM_FACTURE = "NumeroFacture";
		public static final String CODE_CLIENT_FACTURE = "code_client";
		public static final String CLIENT_FACTURE = "client";
		public static final String RGA_FACTURE = "rga";
		public static final String Num_BC = "NumeroBC";
		public static final String RGA_AGENT = "agent";
		public static final String FACTURE_BC_REQUIRED = "isExigenceBC";
		public static final String FACTURE_BL_REQUIRED = "isExigenceBL";
		public static final String REQUIRED_BL_BC_FACTURE = "isExigenceBLAndBC";
		public static final String PATH_START_TAXE = "/api/v1/taxe";
		public static final String PATH_START_LEDGER= "/api/v1/ledger";
		public static final String PATH_START_VENDOR= "/api/v1/vendor";
		public static final String PATH_START_INVOICE2 = "/api/v1/invoiceSage";
		public static final String PARENT_PATH_VENDOR = "/default-domain/workspaces/Entreprise X/Dossier Fournisseur";
		
		
	
}
