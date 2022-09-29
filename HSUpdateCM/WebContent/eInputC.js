/*
File:		einput.js	
Version:	2.1.0
Author:		EMC Captiva
 
(c) Copyright 1996-2010 EMC Corporation. All rights reserved
 
History:
30/01/2007 - AA:	Originally created		 
20/10/2008 - VV:	fixed ESCAN-1325. Version 2.0.0.157
31/10/2008 - VV:	fixed ESCAN-1346. Version 2.0.0.158
 */

var module = null;

//UI Settings
var iShowAction = 1;
var iShowBatchAction = 1;
var iShowIndexAction = 1;
var iShowAdvSettings = 1;

var defBrightness = 129;
var defContrast = 129;

//PDI Control
var oPDIControl = null;
var bScannerConnected = false;
//IA Settings
var oIABatch = null;
var oIAProcesses = null;
var oIAProfiles = null;
var oIATree = null;

var oModule = null;

//Temp variable
var SelectedBatch = null;
var nDocCount = 0;
var g_CurrDocID = null;
var g_curIAValue = "";
var vinSelectedBatch = false;

//Timers
var iTimerID;
var iScanTaskTimer;
var iImportTaskTimer;
var iSendTaskTimer;
var iIADlgLogin;
var iOpenBatchRetFunc;
var iCheckForDownloading;
var iStartDownload;
var iNewIndexBatch;
var iAsyncLoadInitialIndexData;
var iAsyncDownloadImage = null;
var iDownLoadImages;
var iRunAllBatches = null;
//var iUpdateProgress;

var iOfflineModeCheck;
var fileNames;

var arrBatchesToSend;
//This function initialize web controls on main page

var annoWindow;
//CONSTANT
var OFFLINE = "offline";

var g_CurrentFieldID = null;

var http_newpdidoc = new eInputRequest();
var http_getiavalue = new eInputRequest();
var http_advindexing = new eInputRequest();
var http_changeserver = new eInputRequest();
var http_cancelIndexTask = new eInputRequest();
var http_commitIndexDoc = new eInputRequest();
var http_TerminateValidation = new eInputRequest();


var g_bIsIndexed = false;

var g_IsRunAllBatches = false;

var g_bPreserveImporting = false;
var scan_index_validator = null;

var g_IsXMLValue = false;

var ALL_IMAGES_FORMATS = "All Image Files#*.bmp;*.tiff;*.tif;*.jbg;*.jp2;*.jpg;*.pdf;*.pda;*.cal;*.gif;*.dcx;*.mda;*.pcx;*.png;#Windows Bitmap (*.BMP)#*.bmp#TIFF (*.TIF)#*.tif#Plexus TIFF (*.TIFF)#*.tiff#JBIG Files (*.JBG)#*.jbg#JPEG 2000 (*.JP2)#*.jp2#JPEG (*.JPG)#*.jpg#ADOBE (*.PDF)#*.pdf#Calera (*.PDA)#*.pda#CALS (*.CAL)#*.cal#Compuserve (*.GIF)#*.gif#FAX (*.DCX)#*.dcx#MO:DCA Files (*.MDA)#*.mda#Paintbrush (*.PCX)#*.pcx#Portable Network Graphics (*.PNG)#*.png#All Files(*.*)#*.*##";
var ALL_FILES = "All Files(*.*)#*.*##";

var DEFAULT_LEVELS_SETTINGS_XML = "<ArrayOfLevel><Level><Name>@(BatchName)</Name><LevelNum>7</LevelNum><Flags>0</Flags></Level><Level><Name>Level6</Name><LevelNum>6</LevelNum><Flags>0</Flags></Level><Level><Name>Level5</Name><LevelNum>5</LevelNum><Flags>0</Flags></Level><Level><Name>Level4</Name><LevelNum>4</LevelNum><Flags>0</Flags></Level><Level><Name>Stack</Name><LevelNum>3</LevelNum><Flags>0</Flags></Level><Level><Name>Folder</Name><LevelNum>2</LevelNum><Flags>15</Flags></Level><Level><Name>Document</Name><LevelNum>1</LevelNum><Flags>15</Flags></Level><Level><Name>p.</Name><LevelNum>0</LevelNum><Flags>11</Flags></Level></ArrayOfLevel>";
var DEFAULT_RESCAN_LEVELS_SETTINGS_XML = "<ArrayOfLevel><Level><Name>@(BatchName)</Name><LevelNum>7</LevelNum><Flags>0</Flags></Level><Level><Name>Level6</Name><LevelNum>6</LevelNum><Flags>0</Flags></Level><Level><Name>Level5</Name><LevelNum>5</LevelNum><Flags>0</Flags></Level><Level><Name>Level4</Name><LevelNum>4</LevelNum><Flags>0</Flags></Level><Level><Name>Stack</Name><LevelNum>3</LevelNum><Flags>0</Flags></Level><Level><Name>Folder</Name><LevelNum>2</LevelNum><Flags>1</Flags></Level><Level><Name>Document</Name><LevelNum>1</LevelNum><Flags>1</Flags></Level><Level><Name>p.</Name><LevelNum>0</LevelNum><Flags>11</Flags></Level></ArrayOfLevel>";

var IsPageModified = false;
var IsAnnotationsModified = false;

var g_strLastScanError = "";

var g_CancelIndexTaskFailed = true;
var g_CommitIndexDocFailed = true;
var g_StartAdvIndexingFailed = true;

var g_iLastUsedProcessID = null;

RegExp.escape = function(text) {
    if (!arguments.callee.sRE) {
        var specials = [
        '/', '.', '*', '+', '?', '|',
        '(', ')', '[', ']', '{', '}', '\\'
        ];
        arguments.callee.sRE = new RegExp(
            '(\\' + specials.join('|\\') + ')', 'g'
            );
    }
    return text.replace(arguments.callee.sRE, '\\$1');
}

function fLoadMainPage() {
    return fLoadMainPageEx( true );
}

function fLoadMainPageEx( IsMouseWheelDisabled ) {
    if (document.layers) {
        document.captureEvents(Event.MOUSEDOWN);
        document.onmousedown=clickNS4;
    }
    else if (document.all&&!document.getElementById) {
        document.onmousedown=clickIE4;
    }
    
    document.oncontextmenu = new Function("return false");
    document.onmousewheel = HandleMouseWheel;
    
    document.onkeydown = HandleKeyDown; // Capture keys
    
    return true;
}


function OnControlPanelLoad() {
    document.body.style.cursor="wait";
    if ( getControlPanel().iTimerID ) {
        getControlPanel().clearTimeout( getControlPanel().iTimerID );
        getControlPanel().iTimerID = null;
    }
    
    getControlPanel().iTimerID = getControlPanel().setTimeout("Setup()", 100);
}

function Setup() {
    if ( window.top.StartCheckFailed == true )
        return false;
    
    if ( window.top.frames["ImageViewPanelFrame"].document.getElementById("PDICtl") ) {
        document.body.style.cursor="auto";

        //UI Setup
		window.top.g_Module.Setup();

		if(getUIMode() == SCAN_MODE || getUIMode() == RESCAN_MODE)
		{
			scan_index_validator  = new Validator();

			scan_index_validator.Initialize( document.getElementById("sc3") );
		}
        
        oPDIControl = new PDIControl( window.top.frames["ImageViewPanelFrame"].document.getElementById("PDICtl") );
        oPDIControl.Initialize();
        
        try {
            SetControls(
				oPDIControl.GetScanner(),
				oPDIControl.GetContext(),
				oPDIControl.GetAppOptions(),
				window.top.g_RouterScanModes);

			oPDIControl.GetAppOptions().SetViewerSize(oPDIControl.GetAppOptions().viewerSize);
            SetSelectionFromValue(document.getElementById("selViewerSize"), oPDIControl.GetAppOptions().viewerSize);
        }
        catch(e) {
        }
        
        document.getElementById("chkScanRotated").checked = scanOptions.scanRotated ? true : false;

		window.top.OnLogin( "Login to InputAccel Server", "LoginInputAccelServerDlg");
        
    }
    else // Fix for ESCAN-1325
    {
        // Looks like Image View Panel is not yet loaded
        // Try to Setup later
        OnControlPanelLoad();
    }    
}


function ShowHeader(ctlPanel, imgArrow, bShow) {
    if (bShow) {
        imgArrow.src = "../graphics/arrowdown.gif";
        ctlPanel.style.display = "block";
    }
    else {
        imgArrow.src = "../graphics/arrowright.gif";
        ctlPanel.style.display = "none";
    }
}

function clickIE4() {
    if (event.button==2)
        return false;
}

function clickNS4(e) {
    if (document.layers||document.getElementById&&!document.all) {
        if (e.which==2||e.which==3)
            return false;
    }
}

function HandleMouseWheel(evnt) {
    if ( event.ctrlKey ) {
        event.returnValue=false;
        return false;
    }
    
    return true;
}

function HandleKeyDown(evnt) {
    var theKey = String.fromCharCode(event.keyCode);
    var ctrlKey = event.ctrlKey ? 1 : 0;
    var altKey = event.altKey ? 1 : 0;
    var shiftKey = event.shiftKey ? 1 : 0;
    
    var pdiControl = getControlPanel().oPDIControl;
    var oIATree = getTreeViewPanel().oIATree;
    
    switch (event.keyCode) {
        case 116: // F5
            //TODO: Uncomment this in future
            //event.keyCode = 0; // No F5
            if ( navigator.onLine )
                window.top.location.reload(true);
            
            return true;
        case 13: //Enter
            getControlPanel().OnAcceptButton();
            return true;
        case 27: //Esc
            getControlPanel().OnCancelButton();
            return true;
    }
    
    if ( pdiControl != null && oIATree != null ) {
        if ( altKey && shiftKey && !ctrlKey ) //Alt+Shift
        {
            switch (event.keyCode) {
                case 80: //Alt+Shift+P - Print current page
                    getControlPanel().OnPrintSetupDialog("Print","PrintingDlg");
                    break;    
                case 33: //Alt+Shift+Page Up
					getControlPanel().OnSelectPrevPage();
                    break;
                case 34: //Alt+Shift+Page Down
					getControlPanel().OnSelectNextPage();
                    break;
                default:
                    return true;
            }
        }
        else if ( altKey && !shiftKey && !ctrlKey ) //Alt
        {
            switch (event.keyCode) {
                case 34: //Alt+Page Down - Next Page
                    getControlPanel().OnNextPage();
                    break;    
                case 33: //Alt+Page Up - Previouse Page
                    getControlPanel().OnPrevPage();
                    break;    
                default:
                    return true;
            }
        }
        else if ( ctrlKey && !altKey && !shiftKey )// Ctrl
        {
            switch (event.keyCode) {
                //View panel toolbar shortcuts     
                case 73: //Ctrl+I - Zoom In
                    getControlPanel().OnZoomIn();
                    break;
                case 79: //Ctrl+O - Zoom Out
                    getControlPanel().OnZoomOut();
                    break;
                case 70: //Ctrl+F - Fit to Window
                    getControlPanel().OnFitToWindow();
                    break;   
                case 65: //Ctrl+A - Actual Size
                    getControlPanel().OnActualSize();
                    break;
                case 87: //Ctrl+W - Show pan Window
                    getControlPanel().OnPanWindow();
                    break;
                case 76: //Ctrl+L - Rotate Left
                    getControlPanel().OnRotateLeft();
                    break;    
                case 82: //Ctrl+R - Rotate Right
                    getControlPanel().OnRotateRight();
                    break;        
                case 77: //Ctrl+M - Mirror the image
                    getControlPanel().OnMirror();
                    break;            
                case 68: //Ctrl+D - Delete page
                    getControlPanel().OnDeletePage();
                    break;    
                case 83: //Ctrl+S - Save orientation, brightness and contrast
                    getControlPanel().OnSaveModifications();
                    break;
                case 72: //Ctrl+H - The first page of the tree
                    getControlPanel().OnFirstPage();
                    break;    
                case 69: //Ctrl+E - The last page of the tree.
                    getControlPanel().OnLastPage();
                    break;     
                //Tree navigation shortcuts
                case 78: //Ctrl+N - Next page
                    getControlPanel().OnNextPage();
                    break;
                case 80: //Ctrl+P - Previouse page
                    getControlPanel().OnPrevPage();
                    break;
                case 71: //Ctrl+G - Go To a Specific Page Number
                    getControlPanel().OnGoToPage();
                    break;
                case 49: //Ctrl+1 - Next level 1 node
                    getControlPanel().OnNextLevel1Node();
                    break;
                case 50: //Ctrl+2 - Next level 2 node
                    getControlPanel().OnNextLevel2Node();
                    break;
                case 51: //Ctrl+3 - Next level 3 node
                    getControlPanel().OnNextLevel3Node();
                    break;    
                case 52: //Ctrl+4 - Next level 4 node
                    getControlPanel().OnNextLevel4Node();
                    break;        
                case 53: //Ctrl+5 - Next level 5 node
                    getControlPanel().OnNextLevel5Node();
                    break;
                case 54: //Ctrl+6 - Next level 6 node
                    getControlPanel().OnNextLevel6Node();
                    break;    
                default:
                    return true;
                    
            }
        }
        else if ( ctrlKey && !altKey && shiftKey )// Ctrl + Shift
        {
            switch (event.keyCode) {
                case 49: //Ctrl+Shift+1 - Previouse level 1 node
                    getControlPanel().OnPreviousLevel1Node();
                    break;
                case 50: //Ctrl+Shift+2 - Previouse level 2 node
                    getControlPanel().OnPreviousLevel2Node();
                    break;
                case 51: //Ctrl+Shift+3 - Previouse level 3 node
                    getControlPanel().OnPreviousLevel3Node();
                    break;    
                case 52: //Ctrl+Shift+4 - Previouse level 4 node
                    getControlPanel().OnPreviousLevel4Node();
                    break;        
                case 53: //Ctrl+Shift+5 - Previouse level 5 node
                    getControlPanel().OnPreviousLevel5Node();
                    break;
                case 54: //Ctrl+Shift+6 - Previouse level 6 node
                    getControlPanel().OnPreviousLevel6Node();
                    break;
				case 80: //Ctrl+Shift+P - Select the previous page (add to selection)
					getControlPanel().OnSelectPrevPage();
					break
				case 78: //Ctrl+Shift+N - Select the next page (add to selection)
					getControlPanel().OnSelectNextPage();
					break;
				case 72: //Ctrl+Shift+H - Select all pages from current to first
					getControlPanel().OnSelectToFirstPage();
					break
				case 69: //Ctrl+Shift+E -  Select all pages from current to last
					getControlPanel().OnSelectToLastPage();
					break;
                default:
                    return true;
            }
        }
        else if ( altKey && !shiftKey && ctrlKey ) {
			
            if (window.top.frames["IndexFieldsPanelFrame"] != null ) {
                switch (event.keyCode) {
                    case 36: //Alt+Ctrl+HOME - Move to beginning of the first indexing field on the page
                        window.top.frames["IndexFieldsPanelFrame"].OnFirstIndexingField();
                        break;
                    case 35: //Alt+Ctrl+END - Move to beginning of the last indexing field on the page
                        window.top.frames["IndexFieldsPanelFrame"].OnLastIndexingField();
                        break;
                }
            }
        }
        else {
            return true;
        }
        
        if ( !(altKey && ctrlKey) )
            event.keyCode = 555;
        
        event.returnValue=false;
        return false;
    }
    
    
    return true;
}

//Events

function OnCancelDlg() {
//oPDIControl.Show();
}

function OnCancelDlg2() {
    oPDIControl.Show();
}

function OpenDialog( Title, ContentID, width, height, RetFunc, CancelFunc ) {
    if ( oPDIControl != null )
        oPDIControl.Hide();
    
    //window.top.showPopWin(document.getElementById(ContentID), Title, 400, 400, RetFunc, ( CancelFunc != null ) ? CancelFunc : OnCancelDlg );
    window.top.showPopWin(document.getElementById(ContentID), Title, width, height, RetFunc, ( CancelFunc != null ) ? CancelFunc : OnCancelDlg );

}

//function OpenDialog2( Title, ContentID, RetFunc, CancelFunc ) {
//    if ( oPDIControl != null )
//        oPDIControl.Hide();
//
//    window.top.showPopWin(document.getElementById(ContentID), Title, 360, 350, RetFunc, ( CancelFunc != null ) ? CancelFunc : OnCancelDlg );
//}

function ShowBatchActions() {

	window.top.g_Module.ShowBatchActions();


    document.getElementById( 'BatchPanel' ).style.display = "block";
    document.getElementById( 'ControlBatchPanelHeader' ).style.display = "block";
    getControlPanelEl( "AdvControlPanelTbl" ).style.display = "block";
    window.top.frames["ImageViewPanelFrame"].document.getElementById( "ImageViewPanel" ).style.display = "block";
	SetDefaultViewSettings();

}

function HideBatchActions() {

	document.getElementById( 'BatchPanel' ).style.display = "none";

	window.top.g_Module.HideBatchActions();
    
    document.getElementById( 'ControlBatchPanelHeader' ).style.display = "none";
    
    getControlPanelEl( "AdvControlPanelTbl" ).style.display = "none";
    window.top.frames["ImageViewPanelFrame"].document.getElementById( "ImageViewPanel" ).style.display = "none";
    
    if ( oPDIControl != null )
        oPDIControl.Hide();
    
}

function OnNewBatch( Title, ContentID ) {
    if ( FillNewBatchControls( ContentID ) ) {
        if ( oIAProcesses.Count() )
            OpenDialog( Title, ContentID, 400, 400, NewBatchRetFunc );
        else
            alert("There are no installed processes with eScan on InputAccel Server!");
    }
}

var ssoConnectionWasBroken = false;
function OnLoginSSO (skipModuleInit) {
	if(ssoConnectionWasBroken)
		return false;
    ShowWaitCursor ();

    try {
        // open the http connection
        // http.open ( 'GET', LOGIN_PREFIX + '& username =' + username + '& id =' + seed_id + '& hash =' + hash, true);
		var prefix = window.top.g_Module.getSSOLoginPrefix();

        // where to go - > use SSO login processor, just save an SSO session
        http.sendGetRequest (prefix + 'ssologin='+GLOBAL_SSO_ENABLED,handleSSOLogin);

        // window.top.frames [ "ControlPanelFrame"]. LoginRetFunc ( "online");
		if(loggedIn && !skipModuleInit)
	        getControlPanel(). LoginRetFunc (window.top.g_CachedMode ? OFFLINE: "online");
    }
    catch (e) {
        loggedIn = false;
        ShowErrorAlert ( "Exception", e.name, e.number, e.description, null, true);
    }

	if(!loggedIn)
	{
        ShowErrorAlert ( "Error", "Login failed", 0,
			"The system can not connect the InputAccel server. This may occur if eInput Server was not configured properly. Contact administrator.", null, true);
		if(!ssoConnectionWasBroken)
			ssoConnectionWasBroken = true;
		window.close();
		window.setTimeout("ssoConnectionWasBroken=false;OnLoginSSO()", 1000);
	}
	else
	    ShowAutoCursor ();
	
    return loggedIn;
}

function OnLogin( Title, ContentID ) {
    window.top.hidePopWin(false);
	
    var tdWorkOffline = getControlPanelEl("tdWorkOffline");
    if ( getUIMode() == INDEX_MODE || getUIMode() == RESCAN_MODE) {
        if ( tdWorkOffline != null )
            tdWorkOffline.style.display = "none";
    }
    else {
        if ( tdWorkOffline != null )
            tdWorkOffline.style.display = "inline";
    }

    if ( !window.top.g_CachedMode )
        window.top.g_CachedMode = !navigator.onLine;
    //EINPUT-30406 (Reopening): AdvancedIndexing doesn't load departments
	if(isSSOEnabled() && (window.top.AdvancedIndexingDocID != null && window.top.AdvancedIndexingDocID != ""))
		OnLoginSSO(true);

    if ( (window.top.AdvancedIndexingDocID != null && window.top.AdvancedIndexingDocID != "")
		|| window.top.g_CachedMode
		|| (window.top.g_IsOpened && IsLoggedIn(true) && GetCookie(EINPUT_SESSION_ID) != "") ) {
        getControlPanel().LoginRetFunc( window.top.g_CachedMode ? OFFLINE : "online" );

		return;
    }

    var oIaLoginModuleName = getControlPanelEl("IaLoginModuleName");
    if ( oIaLoginModuleName != null ) 
        oIaLoginModuleName.innerText = window.top.g_Module.moduleName;
    
    var oIaLoginModuleVersion = getControlPanelEl("IaLoginModuleVersion");
    if ( oIaLoginModuleVersion != null ) {
        oIaLoginModuleVersion.innerText = EINPUT_VERSION_VALUE;
    }
    
    GetAutocompleteLoginField( getControlPanelEl("selectServer") );
    GetAutocompleteLoginField( getControlPanelEl("selectUserName") );
    GetAutocompleteLoginField( getControlPanelEl("selectDomain") );


    getControlPanelEl("SERVER").value = GetCookie( EINPUT_LOGIN_SERVER );
    getControlPanelEl("USERNAME").value = GetCookie( EINPUT_LOGIN_USERNAME );
    getControlPanelEl("DOMAIN").value = GetCookie( EINPUT_LOGIN_DOMAIN );
    getControlPanelEl("DEPARTMENT").value = GetCookie( EINPUT_LOGIN_DEPARTMENT );

    // try to use silient login
    if (OnSilentLogin () == false)
	{
		if(ssoConnectionWasBroken)
		{
			window.top.ShowSplashScreen();
			return;
		}

		if(!isSSOEnabled())
			window.top.showPopWin(getControlPanelEl(ContentID), Title, 400, 280, LoginRetFunc, null, false);
	}
        
}

function OnLogout()
{
	if(!navigator.onLine)
		return;
	
	if(getControlPanelEl( 'BatchPanel' ).style.display == "none" || //Batch actions are hidden
			getControlPanel().OnCloseBatch(false, false))
		PerformLogout();
}

function CheckBatchCondition()
{
	try {
		var PDIDocument = oPDIControl.GetDocument();
		var PDITags = PDIDocument.GetTags();
		var IsIndexed = ( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_INDEXED)) == "true" ? true : false );

		if ( (getUIMode() == INDEX_MODE || oIABatch.IsAdvancedIndexingMode()) && !IsIndexed ) {
			var bShowAlert = true;
		
			if(getUIMode() == INDEX_MODE && !getControlPanelEl("btnAcceptTask").disabled)
				OnAcceptTask();

			if ( !IsIndexed )
				bShowAlert = !window.top.g_Module.IsBatchComplete(PDIDocument);

			if ( bShowAlert && !confirm("Indexing of this batch is not complete!\nSend it anyway?") )
				return false;
		}

		if ( getUIMode() == RESCAN_MODE )
		{
			var IsRescanned = ( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_RESCANNED)) == "true" ? true : false );
			if(!IsRescanned)
				{
					IsRescanned = window.top.g_Module.IsBatchComplete(PDIDocument);
					if(!IsRescanned &&!confirm("Rescaning of this batch is not complete!\nSend it anyway?") )
						return false;
				}
		}
	}
	catch(e) {
		alert( e.description );
	}
	return true;
}


function OnBatchSend() {

//IMAGE ACCESS MOD
		if(oModule.currentBatch.ProcessName == "GDO" && (oModule.name == "eScan" || oModule.name == "Scan"))
		{
			var error = validateBatchStructure();
			if(error.length > 0)
			{
				if(!confirm(error + "\n\nDo you want to continue submitting the batch anyway?\n\nPress OK to submit\nPress Cancel to stay in scan"))
				{
					return;
				}
			}
		}
//IMAGE ACCESS MOD - END

    if ( IsPageModified || IsAnnotationsModified ) {
        OnSaveModifications();
        IsPageModified = false;
        IsAnnotationsModified = false;
    }

    if ( confirm("Do you wish to send the current batch?") ) {
		if(!CheckBatchCondition())
			return;
        arrBatchesToSend = new Array(1);
        arrBatchesToSend[0] = oPDIControl.GetDocument().GetGUID();
        oPDIControl.Hide();
        if ( OnCloseBatch(true, false) ) {
			if(oPDIControl.GetDocument().IsModified()) {
				if(!confirm("Error occured. The batch  was not saved correctly. Are you sure you want to send it?"))
					return;
			}

            SendBatches();
			//if(!g_IsRunAllBatches)
			//	OnVinSingleBatch("Run Single Batch", "OpenInputAccelBatchDlg");
        }
        else {
            arrBatchesToSend = null;
            oPDIControl.Show();
        }
    }
}

function OnOpenBatch( Title, ContentID ) {
    if(window.top.isDownloading)
	{
		alert("Could not perform the operation while a batch is being sent");
		return;
	}
    g_bIsIndexed = true;
    if ( LoadBatches( false, true ) )
	{
        OpenDialog( Title, ContentID, 400, 400, OpenBatchRetFunc );
	}
	else
		if(nDocCount == 0)
			alert("No batches to select");

}

function OnNextRescanPages()
{
	var tree = getTreeViewPanel().oIATree;

	tree.OnBeforeHighlighting();
	
	if(window.top.g_Module.CheckSelectedValues(
		tree,
			oPDIControl.GetDocument(), true, false))
	{
		window.top.g_Module.DeclineSelected(
			tree,
			oPDIControl);
	}

	window.top.g_Module.FindAllRescanPages(
			tree,
			getControlPanel().oPDIControl);
			
	var selection = getTreeViewPanel().oIATree.GetSelectedItems();
	if(selection.length > 0)
	{
		tree.SetCurrentlyActiveItem(selection[0]);
		tree.OnHighlightItem();
	}
}

function OnImportPages( Title, ContentID, bPreserveImporting, filter, attachment ) {
    if ( !bPreserveImporting ) {
        if ( IsPageModified || IsAnnotationsModified ) {
            OnSaveModifications();
            IsPageModified = false;
            IsAnnotationsModified = false;
        }
    }

	if(!attachment && !window.top.g_Module.CheckSelectedValues(
			getTreeViewPanel().oIATree,
			oPDIControl.GetDocument(), false, true))
	{
		//alert("The selected nodes have different Needs Rescan values");
		return false;
	}

    g_bPreserveImporting = bPreserveImporting;
    if ( filter == "" || filter == null )
        filter = ALL_IMAGES_FORMATS;
    
    fileNames = oPDIControl.PDICtl.BrowseWithFilter( filter );
    if (fileNames.length > 0) {
        OnOpenProgressDlg( Title, ContentID );
        
        if ( getControlPanel().iImportTaskTimer ) {
            getControlPanel().clearTimeout( getControlPanel().iImportTaskTimer );
            getControlPanel().iImportTaskTimer = null;
        }
        
        getControlPanel().iImportTaskTimer = getControlPanel().setTimeout("getControlPanel().AsyncImportFile()", 100);
        
    //AsyncImportFile();
    } 
    else {
        return false;
    }
    
    return true;
}

function AsyncImportFile() {
    try {
        getControlPanel().g_strLastScanError = "";
        getControlPanel().oPDIControl.GetScanner().Import( getControlPanel().fileNames, getControlPanel().g_bPreserveImporting);
        
        if ( getControlPanel().g_strLastScanError != "" )
            throw getControlPanel().g_strLastScanError;
    } 
    catch (e) {
        if ( typeof(e) == 'string' )
            alert( "Error during import!\nDescription: " + e );
        else
            ShowErrorAlert("Error during import", e.name, e.number, e.description, getControlPanel().oPDIControl.GetContext(), true);
    }
    
    getControlPanel().g_strLastScanError = "";
    getControlPanel().CloseCancelWindow();//normal close
    getControlPanel().fileNames = "";

}

function OnScanPages( Title, ContentID ) {
    try {
        if ( IsPageModified || IsAnnotationsModified ) {
            OnSaveModifications();
            IsPageModified = false;
            IsAnnotationsModified = false;
        }

		if(!window.top.g_Module.CheckSelectedValues(
			getTreeViewPanel().oIATree,
			oPDIControl.GetDocument(), false, true))
		{
			//alert("The selected nodes have different Needs Rescan values");
			return false;
		}

		if ( oPDIControl.IsValidScanner() ) {
            OnOpenProgressDlg( Title, ContentID );
            
            if ( getControlPanel().iScanTaskTimer ) {
                getControlPanel().clearTimeout( getControlPanel().iScanTaskTimer );
                getControlPanel().iScanTaskTimer = null;
            }
            
            getControlPanel().g_bPreserveImporting = false;
            getControlPanel().iScanTaskTimer = getControlPanel().setTimeout("getControlPanel().AsyncScanPages()", 100);
        }	
        else {
            alert("Please select a scanner before scanning.");
        }	
    } 
    catch (e) {
        ShowErrorAlert("Error during scanning.", e.name, e.number, e.description, PDIContext, true);
    }
}

function AsyncScanPages() {
    try {
        getControlPanel().g_strLastScanError = "";
        getControlPanel().oPDIControl.GetScanner().Scan();
        
        if ( getControlPanel().g_strLastScanError != "" )
            throw getControlPanel().g_strLastScanError;
    }
    catch (e) {
        if ( typeof(e) == 'string' )
            alert( "Error during scanning!\nDescription: " + e );
        else
            ShowErrorAlert("Error during scanning", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }	
    
    getControlPanel().g_strLastScanError = "";
    getControlPanel().CloseCancelWindow();//normal close

//CloseCancelWindow();
}

function OnCloseBatch(bSkipConfirmation, downloadNext) {
    if ( IsPageModified || IsAnnotationsModified ) {
        OnSaveModifications();
        IsPageModified = false;
        IsAnnotationsModified = false;
    }
    
    window.top.AdvancedIndexingDocID = "";
    window.top.SkipLogout = false;
    
    try {
        if ( bSkipConfirmation || confirm( ( (getControlPanel().oIABatch.IsAdvancedIndexingMode() && (getUIMode() == INDEX_MODE) ) ? "Do you wish to finish indexing the current batch?" : "Do you wish to close the current batch?") ) ) {
            if ( OnSaveBatch(true) ) {
                
                if ( getControlPanel().oIABatch.IsAdvancedIndexingMode() && (getUIMode() == INDEX_MODE) ) {
                    ClearAll(bSkipConfirmation);
                    window.top.OpenScanModule( getControlPanel().oPDIControl.GetDocument().GetGUID() );

					getControlPanel().oPDIControl.GetDocument().ResetRuntimeMarkup();

                    return true;
                }
                
                HideBatchActions();

				window.top.g_Module.InitializeClosedWindowTitle();
                
				getControlPanel().oPDIControl.GetDocument().ResetRuntimeMarkup();
                ClearAll(bSkipConfirmation);
				if(downloadNext)
				{
					if(g_IsRunAllBatches)
						ContinueRunAllBatches( false );
					else
						OnVinSingleBatch("Run Single Batch", "OpenInputAccelBatchDlg");
				}
                return true;
            }
        }
    }
    catch ( e ) {
        ShowErrorAlert("Error while closing the batch", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }
    
    return false;
}


function OnSaveBatch(clean) {
    if ( IsPageModified || IsAnnotationsModified ) {
        OnSaveModifications();
        IsPageModified = false;
        IsAnnotationsModified = false;
    }
    
    if ( getControlPanelEl("BatchPanel").style.display != "block" )
        return false;
    
    var PDIDoc = oPDIControl.GetDocument();
    if ( PDIDoc ) {
        var PDITags = PDIDoc.GetTags();
        if ( PDITags ) {
            var TagState = PDITags.GetTag("PDI_TAG_STATE").toLowerCase();
            if ( TagState != "unsent" && TagState != "edit mode" )
                return false;
            
            try {
                //TODO: save current index fields
                var oCurrentlyActiveItem = getTreeViewPanel().oIATree.GetCurrentlyActiveItem();
                if ( oCurrentlyActiveItem != null ) {
					var level = Number(oCurrentlyActiveItem.getAttribute("Level"));
					var nodeID = oCurrentlyActiveItem.id;
					
					if(level == 0)
						nodeID = Number(oCurrentlyActiveItem.getAttribute("ImgIndex"));

	                if ( !OnChangeCurrentPage( nodeID, level ) )
	                   return false;

                }
            }
            catch ( e ) {
                return false;
            }
            
            if ( getControlPanel().oIABatch.IsAdvancedIndexingMode() || (getUIMode() == INDEX_MODE) || getUIMode() == RESCAN_MODE)
                window.top.g_Module.IsBatchComplete(PDIDoc);

           
            //getTreeViewPanel().oIATree.ClearSelection();
            
            if ( getUIMode() == SCAN_MODE) {
                PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_TREE), getTreeViewPanel().oIATree.SerializeToXML() );
                //PDI Currently does not support this tag
                //var  strValue = PDITags.GetTag("PDI_TAG_MODIFICATION_DATE_UTC");
                //PDITags.SetTag(AddStatusPrefix(EINPUT_LEVEL7_MODIFICATION_DATE), strValue);
                
                //set current date  in UTC format as batch modification date
                PDITags.SetTag(AddStatusPrefix(EINPUT_LEVEL7_MODIFICATION_DATE), MakeUTC());
            }
			else if (getUIMode() == RESCAN_MODE)
			{
                PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_TREE), getTreeViewPanel().oIATree.SerializeToXML() );
                PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_TREE_HISTORY), getTreeViewPanel().oIATree.GetHistory() );
                PDITags.SetTag(AddStatusPrefix(EINPUT_LEVEL7_MODIFICATION_DATE), MakeUTC());
			}	
            
        }
		if(!FlushTags( clean))
			return false;
    }
    
    return true;
}

function FlushTags(clean)
{
	ShowWaitCursor();
	try
	{
		var PDIDoc = oPDIControl.GetDocument();
		if(PDIDoc != null)
			 PDIDoc.Flush(clean);
	}
	catch(ex)
	{
		ShowErrorAlert("Error while saving the batch:", ex.name, ex.number, ex.description, null, true);
		ShowAutoCursor();
		return false;
	}
	ShowAutoCursor();
	return true;
}

function OnNewBatchDlgClose() {

    if ( !Check( document.getElementById("BatchName"), '\\"/:*?<>|.', 'Batch name cannot contain:') )
        return false;
    
    if ( !Check( document.getElementById("BatchNameSchema"), '\\"/:*?<>|.', 'Batch naming schema cannot contain:') )
        return false;
    
    if ( !document.getElementById("BatchName").disabled && document.getElementById("BatchName").value == "" ) {
        alert("Enter Batch name");
        document.getElementById("BatchName").focus();
        return false;
    }	

    var oProcesses = getControlPanel().oIAProcesses;
    var oNewBatch = oProcesses.GetByID( document.getElementById("Process").value );
    
    var bConfirmed;
    if ( oNewBatch.IsNoSetupSettings() ) {
        if ( !navigator.onLine )
            bConfirmed = confirm("The settings for the selected process were not loaded from InputAccel in online mode.\nContinue with the default process settings?");
        else
            bConfirmed = confirm("eScan settings are inherited from the selected process and cannot be set up at the batch level for eScan.\nContinue with the existing process settings?");
    }
    else
        bConfirmed = true;
    
    if ( !bConfirmed )
        return false;
    
    oNewBatch.SetBatchName( document.getElementById("BatchName").value );
    oNewBatch.SetPriority( document.getElementById("BatchPriority").value );
    oNewBatch.SetDescription( document.getElementById("Description").value );
    oNewBatch.SetPrivateBatch( document.getElementById("PrivateBatch").checked );
    
    if ( document.getElementById("BatchNameSchema").value != "" )
        oNewBatch.SetBatchNamingSchema( document.getElementById("BatchNameSchema").value );
    
    document.getElementById("popupFrame").returnVal = oNewBatch;
    return true;
}


function OnOpenBatchDlgClose() {
    getControlPanel().ShowWaitCursor();
    this.className = "buttons";
    
    var arrBatches = document.getElementById("batches");
    if ( arrBatches.value == "" || !arrBatches.value )
        return false;
    
    var arSelectedBatches = new Array();
    
    if ( arrBatches.multiple == false ) {
        var selBatch = arrBatches.options[arrBatches.selectedIndex];
        if ( window.event.srcElement.sourseIndex != selBatch.sourseIndex )
            return false;
        
        arSelectedBatches.push( unescape(document.getElementById("batches").value) );
    }
    else {
        while (arrBatches.selectedIndex != -1) {
            arSelectedBatches.push( unescape(arrBatches.options[arrBatches.selectedIndex].value) );
            arrBatches.options[arrBatches.selectedIndex].selected = false;
        }
    }	
    
    document.getElementById("popupFrame").returnVal = arSelectedBatches;
    
    if ( getControlPanel().iOpenBatchRetFunc ) {
        getControlPanel().clearTimeout( getControlPanel().iOpenBatchRetFunc );
        getControlPanel().iOpenBatchRetFunc = null;
    }
    
    getControlPanel().iOpenBatchRetFunc = getControlPanel().setTimeout("getControlPanel().AsyncOpenBatchDlgClose()", 100);
}

function AsyncOpenBatchDlgClose() {
    window.top.hidePopWin(true);
}

function OnLoginIADlgLogin() {
    window.top.ShowWaitCursor();
    this.className = "buttons";
    
    //TODO: Check is browser offline
    //Login IA server...
    if ( document.getElementById("bWorkOffline").checked )
        document.getElementById("popupFrame").returnVal = OFFLINE;
	else
        document.getElementById("popupFrame").returnVal = "online";
    
    if ( !navigator.onLine ) {
        if ( window.top.iIADlgLogin ) {
            window.top.clearTimeout( window.top.iIADlgLogin );
            window.top.iIADlgLogin = null;
        }
        window.top.iIADlgLogin = window.top.setTimeout("OnLoginIADlgWorkOffline()", 100);
    }    
    else {
        if ( window.top.iIADlgLogin ) {
            window.top.clearTimeout( window.top.iIADlgLogin );
            window.top.iIADlgLogin = null;
        }
        
        window.top.iIADlgLogin = window.top.setTimeout("AsyncIADlgLogin()", 100);
    }    
}

function AsyncIADlgLogin() {
    try {
        validateLogin();
    }
    catch(e) {
        ShowErrorAlert("Error during login to the InputAccel Server", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }
}

function OnLoginIADlgClose() {
    
    window.top.close();
}

function OnLoginIADlgWorkOffline() {
    document.getElementById("popupFrame").returnVal = OFFLINE;
    window.top.hidePopWin(true);
}

function DisableButtonWhenOffline(buttonName)
{
    var btn = getControlPanelEl(buttonName);
	if(btn != null)
	{
		if ( window.top.g_OfflineStateAtStartUp )
			btn.disabled = true;
		else
		{
	        btn.disabled = !navigator.onLine;
		    if ( btn.disabled )
			    btn.className='buttons';
		}
	}
}


var offlineAlertWasShown = false;
function OnCheckOffline()
{
	if(window.top.g_OfflineStateAtStartUp && navigator.onLine && !offlineAlertWasShown)
	{
		alert("The module works in offline mode. To work in online mode refresh the module");
		offlineAlertWasShown = true;
	}
	if(!navigator.onLine)
		offlineAlertWasShown = false;
	
	DisableButtonWhenOffline("btnRunAllBatches");

	DisableButtonWhenOffline("btnSend");

    var btn = getControlPanelEl("logoutlinkid");
    if ( btn != null )
        btn.style.visibility =
			window.top.g_OfflineStateAtStartUp || !navigator.onLine ?
			"hidden" : "visible";
}

function OnSelectFilter() {
    
}

function OnZoomIn() {
    var radioCustomSize = getControlPanelEl("radioCustomSize");
    if ( radioCustomSize != null )
        radioCustomSize.checked = true;
    
    oPDIControl.Scale( 10 );
}

function OnZoomOut() {
    var radioCustomSize = getControlPanelEl("radioCustomSize");
    if ( radioCustomSize != null )
        radioCustomSize.checked = true;
    
    oPDIControl.Scale( -10 );	
}

function OnActualSize() {
    var radioActualSize = getControlPanelEl("radioActualSize");
    if ( radioActualSize != null )
        radioActualSize.checked = true;
    
    oPDIControl.One2One();
}

function OnPanWindow() {
    oPDIControl.ShowPanWindow();
}

function CheckOrientation()
{
	if(document.getElementById("selOrientation").selectedIndex != 0 &&
		!confirm("Rotations are enabled only in Portrait orientation. Orientation will be changed to portrait."))
		return false;
	return true;
}

function OnRotateLeft() {
	if(!CheckOrientation())
		return;

	if(document.getElementById("selOrientation").selectedIndex != 0)
	{
		document.getElementById("selOrientation").selectedIndex = 0;
		OnSetOrientation();
	}

	oPDIControl.RotateLeft();
	MarkPageAsModified();
}

function OnRotateRight() {
	if(!CheckOrientation())
		return;

	if(document.getElementById("selOrientation").selectedIndex != 0)
	{
		document.getElementById("selOrientation").selectedIndex = 0;
		OnSetOrientation();
	}
	
    oPDIControl.RotateRight();
    MarkPageAsModified();
}

function OnFitToWindow() {
    var radioFitToWindow = getControlPanelEl("radioFitToWindow");
    if ( radioFitToWindow != null )
        radioFitToWindow.checked = true;

    oPDIControl.FitPage();
}

function OnInvert( invert ) {
    oPDIControl.Invert( invert );
}

function OnSetOrientation() {
    if ( IsPageModified || IsAnnotationsModified )
        OnSaveModifications();

    oPDIControl.Orientation( GetSelectedValue(document.getElementById("selOrientation")) );
}

function OnBrightness( val ) {
    if(oPDIControl.SetBrightness( 0, val )) {
        MarkPageAsModified();
    }
}

function OnContrast( val ) {
    if(oPDIControl.SetContrast( 0, val )) {
        MarkPageAsModified();
    }
}

function OnMirror() {
	if( document.getElementById("selOrientation").selectedIndex != 0 &&
		!confirm("Mirror option is enabled only for Portrait orientation. Orientation will be changed to portrait."))
		return;

	if(document.getElementById("selOrientation").selectedIndex != 0)
	{
		document.getElementById("selOrientation").selectedIndex = 0;
		OnSetOrientation();
	}
	
    oPDIControl.Mirror();
    MarkPageAsModified();
}

function MarkPageAsModified() {
    IsPageModified = true;
}

function UpdateAfterModification() {
        getControlPanel().SetSliderValue(
            "brightness_slider", "brightness_display", defBrightness);
        getControlPanel().SetSliderValue(
            "contrast_slider", "contrast_display", defContrast);
        IsPageModified = false;
        IsAnnotationsModified = false;
		getTreeViewPanel().oIATree.GenerateThumbnailForActiveItem();
}

function OnRevertModifications() {
    if ( oPDIControl.RevertModifications() ) {
        UpdateAfterModification();
    }
}

function OnSaveModifications() {
    if ( oPDIControl.SaveModifications() ) {
        UpdateAfterModification();
    }
}

function OnPrintSetupDialog( Title, ContentID ) {
	//escan-1400
	var button = getImageViewPanel().document.getElementById("printButton");
	if(button != null)
		button.focus();
	
    if ( oPDIControl.GetDocument() != null && oPDIControl.GetPageCount() > 0 ) {
        OpenDialog( Title, ContentID, 360, 350, PrintRetFunc, OnCancelDlg2 );
    }
    else {
        alert("No images to print!");
    }
}

function PrintRetFunc( printSettings ) {
    if ( printSettings != null ) {
        try {
            getControlPanel().oPDIControl.Print( printSettings.nFitType,
                printSettings.nOrientation,
                printSettings.bMirrored,
                printSettings.bAnnotations,
                printSettings.bPrintCurrentPage );
        }
        catch( e ) {
            alert("Select a printer before printing.");
        }
    }
    else {
        alert("No printer settings!");
    }
    oPDIControl.Show();
}

function OnPrint() {
    ShowWaitCursor();
    
    var printSettings = new PrintSettings();
    
    var selPrintingScaling = document.getElementById("selPrintingScaling");
    if ( selPrintingScaling != null ) {
        if ( selPrintingScaling.selectedIndex != -1 ) 
            printSettings.nFitType = selPrintingScaling.options[selPrintingScaling.selectedIndex].value;
    }
    
    var selPrintingOrientation = document.getElementById("selPrintingOrientation");
    if ( selPrintingOrientation != null ) {
        if ( selPrintingOrientation.selectedIndex != -1 ) 
            printSettings.nOrientation = selPrintingOrientation.options[selPrintingOrientation.selectedIndex].value;
    }
    
    var chkPrintWithAnnotations = document.getElementById("chkPrintWithAnnotations");
    if ( chkPrintWithAnnotations != null )
        printSettings.bAnnotations = chkPrintWithAnnotations.checked;
    
    if ( printSettings.bAnnotations ) {
        getControlPanel().oPDIControl.SaveAnnotations();
        getControlPanel().IsAnnotationsModified = false;
    }
    
    var chkPrintMirrored = document.getElementById("chkPrintMirrored");
    if ( chkPrintMirrored != null )
        printSettings.bMirrored = chkPrintMirrored.checked;
    
    var PrintCurrentPage = document.getElementById("PrintCurrentPage");
    if ( PrintCurrentPage != null )
        printSettings.bPrintCurrentPage = PrintCurrentPage.checked;
    
    document.getElementById("popupFrame").returnVal = printSettings;
    window.top.hidePopWin(true);
    
    ShowAutoCursor();
}

function OnPrinterSetup() {
    ShowWaitCursor();
    
    getControlPanel().oPDIControl.PrintSetup();
    
    ShowAutoCursor();
}

function UploadBatchRetFunc( Batch2Send ) {
    //oPDIControl.Show();
    if ( Batch2Send.length ) {
        document.getElementById("btnSend").disabled = true;
        
        arrBatchesToSend = new Array( Batch2Send.lenght );
        for ( var i = 0; i < Batch2Send.length; i++ ) {
            arrBatchesToSend[i] = Batch2Send[i].split("|")[2]; //doc guid
        }
    }	
    SendBatches();
}


var isDownloading = false;
function SendBatches() {
	if(!CheckConnectionWithLogin(true))
		return;

    window.top.sendDoc = null;
    var SendWindow = window.open(FRAME_SEND_URL_PREFIX, '_blank',"center=true,height=200,width=400,status=off,toolbar=off,titlebar=no,resizable=no,scrollbars=no,menubar=no");
    if ( SendWindow != null ) {
        window.top.selectedDocs = arrBatchesToSend;
		window.top.sendDoc = true;
		window.top.isDownloading = true;
        if ( window.focus )
            SendWindow.focus();
    }
    else {
        alert("The Send or Receive window was blocked. Allow pop-ups from this website to send or receive documents.");
    }
//window.top.document.getElementById("SendProgressPanel").style.display = "block";
//window.top.frames["SendProgressPanelFrame"].Send( arrBatchesToSend );
}


function OnUploadFinished() {
	window.top.isDownloading = false;
    arrBatchesToSend = null;
    document.getElementById("btnSend").disabled = false;

    if ( getControlPanel().iCheckForDownloading ) {
        getControlPanel().clearTimeout( getControlPanel().iCheckForDownloading );
        getControlPanel().iCheckForDownloading = null;
    }
    getControlPanel().iCheckForDownloading = getControlPanel().setTimeout("getControlPanel().CheckForDownloading()", 100);
}

function CheckForDownloading()
{
	if(!g_IsRunAllBatches)
		OnVinSingleBatch("Run Single Batch", "OpenInputAccelBatchDlg");
	else
		ContinueRunAllBatches(false);
}

function OnDownloadFinished() {

    ReportDownloadProgress("Batch downloaded successfully.");
    if ( getControlPanel().iNewIndexBatch ) {
        getControlPanel().clearTimeout( getControlPanel().iNewIndexBatch );
        getControlPanel().iNewIndexBatch = null;
    }
    getControlPanel().iNewIndexBatch = getControlPanel().setTimeout("getControlPanel().NewBatchRetFunc()", 100);
    ReportDownloadProgress("Opening batch...");    
}

function OnLoadInitialIndexData() {
    ReportDownloadProgress("Load initial indexing data...");
    if ( getControlPanel().iAsyncLoadInitialIndexData ) {
        getControlPanel().clearTimeout( getControlPanel().iAsyncLoadInitialIndexData );
        getControlPanel().iAsyncLoadInitialIndexData = null;
    }
    getControlPanel().iAsyncLoadInitialIndexData = getControlPanel().setTimeout("getControlPanel().AsyncLoadInitialIndexData()", 100);
}

function OnLoadInitialRescanData() {
    ReportDownloadProgress("Load initial rescan data...");
    if ( getControlPanel().iAsyncLoadInitialRescanData ) {
        getControlPanel().clearTimeout( getControlPanel().iAsyncLoadInitialRescanData );
        getControlPanel().iAsyncLoadInitialRescanData = null;
    }
    getControlPanel().iAsyncLoadInitialRescanData = getControlPanel().setTimeout("getControlPanel().AsyncLoadInitialRescanData()", 100);
}
function AsyncLoadInitialIndexData() {
    try {
		var FailedOnLoadIndexData = false;
        oPDIControl = getControlPanel().oPDIControl;
        
        if ( getControlPanel().g_CurrDocID != null ) {
            //oPDIControl.RefreshDocuments();
            var bDocumentExist = oPDIControl.LoadDocument( getControlPanel().g_CurrDocID );
            var PDIDocument = null;
            if ( bDocumentExist )
                PDIDocument = oPDIControl.GetDocument();
            
            if ( PDIDocument ) {
                var IndexXMLDoc = null;
                var IndexXMLRoot = null;
                var oValidator = window.top.frames["IndexFieldsPanelFrame"].g_objValidator;
                if ( oValidator != null ) {
                    IndexXMLDoc = GetXMLParser( oValidator.GetIndexFieldsXML() );
                    IndexXMLRoot = IndexXMLDoc.documentElement;  
                }
                
                var DocPDITags = PDIDocument.GetTags();
                
                if ( DocPDITags != null )
                    DocPDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_LOAD_INDEX_DATA );
                
                var TreeXML = DocPDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE)) ;
                if ( TreeXML != null ) {
                    var doc = GetXMLParser( TreeXML );
                    if ( doc != null && doc.documentElement != null ) {
                        var Tree = doc.documentElement;
                        var nNodeCount = Tree.childNodes.length;
                        
                        //for each node
                        for ( var j = 0; j < nNodeCount; j++ ) {
                            var Node = Tree.childNodes(j);
                            var Level = Number(Node.getAttribute("Level"));
                            var id = Node.getAttribute("id");
                            var index = Number(Node.getAttribute("imgIndex"));
                            var PDITags = null;
                            var IndexFieldsLevels = "<index_fields>";
                            
                            if ( Level == 0 ) //page level
                            {
                                var CurPage = PDIDocument.GetPageByIndex( index );
                                if ( CurPage != null) {
                                    if(oIABatch.GetIndexSettings().GetImageStreamingType()==0)
                                        RequestOCRDataForPage(CurPage, id);
                                }
                            }
                            
                            if ( IndexXMLRoot != null ) {
                                var nCount = IndexXMLRoot.childNodes.length;
                                for ( var i = 0; i < nCount; i++ ) {
                                    var index_item = IndexXMLRoot.childNodes(i);
                                    var numLevel = Number(index_item.getElementsByTagName("level")[0].text);
                                    var prevValue = index_item.getElementsByTagName("value")[0].text;
                                    if ( prevValue == null )
                                        prevValue = "";
                                    
                                    if ( numLevel == Level ) {
                                        g_curIAValue = null;
                                        
                                        //if ( index_item.getElementsByTagName("value")[0].text == null || 
                                        //    index_item.getElementsByTagName("value")[0].text == "" )
                                        //{
                                        if ( getControlPanel().oIABatch.IsAdvancedIndexingMode() ) {
                                            g_curIAValue = "";
                                        }
                                        else {
                                            getControlPanel().g_IsXMLValue = false;
                                            http_getiavalue.sendGetRequest(INDEX_GET_NODE_VALUE_PREFIX + "&id=" + oIABatch.GetProcessID() + "&node=" + id + "&name=" + escape(index_item.getElementsByTagName("name")[0].text), GetIAValueCallback);
                                        }

                                        if ( g_curIAValue != null ) {
                                            if ( g_curIAValue != "" )
                                                index_item.getElementsByTagName("value")[0].text = g_curIAValue;
                                        }
                                        else {
                                            FailedOnLoadIndexData = true;
                                        //index_item.getElementsByTagName("value")[0].text = "";
                                        }
                                        
                                        g_curIAValue = null;
                                        //}
                                        
                                        IndexFieldsLevels += index_item.xml;
                                        index_item.getElementsByTagName("value")[0].text = prevValue;
                                    }
                                }
                            }
                            
                            IndexFieldsLevels += "</index_fields>";
                            
                            if ( Level == 0 ) {
                                if ( PDITags != null )
                                    PDITags.SetTag(AddStatusPrefix(EINPUT_PAGE_INDEX_FIELDS), IndexFieldsLevels );
                            }
                            else {
                                if ( DocPDITags != null )
                                    DocPDITags.SetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + id), IndexFieldsLevels );
                            }
                            
                            if ( FailedOnLoadIndexData ) {
                                if ( !confirm("Continue loading initial indexing data?") )
                                    throw "Break";
                            }
                        }
                    }
                }
                
                if ( !FailedOnLoadIndexData )
                    DocPDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_READY );
            }
        }
    }
    catch(e) {
        if ( typeof(e) != 'string' )
            ShowErrorAlert("Error while downloading indexing fields data from the InputAccel Server", e.name, e.number, e.description, oPDIControl.GetContext(), true);
        
        alert("Batch was not successfully loaded from the eInput Server!\nTo continue loading the initial indexing data, reopen the batch in eIndex.");
    }  
    
    CloseDownloadProgress();
    ShowBatchActions();

    window.top.frames["IndexFieldsPanelFrame"].document.body.style.visibility = "visible";

	CallAsyncresetHighlightedItem();

	CallDownloadImages();
}

function LoadIndexFieldsForNode(Node, PDIDocument)
{
	var Level = Number(Node.getAttribute("Level"));

	var IndexHTML = oIABatch.GetIndexFieldsHTMLByLevel(Number(Level));
	if ( IndexHTML == null || IndexHTML == "" )
		return false;

	var id = Node.getAttribute("id");
	var imgIndex = Node.getAttribute("imgIndex");
	var index_fieldsXML = "<index_fields>";
	var hasValue = false;
	var PDITags = null;
	if(Level == 0)
	{
		var CurPage = PDIDocument.GetPageByIndex( Number(imgIndex) );
		if ( CurPage != null)
			PDITags = CurPage.GetTags();
	}
	var DocPDITags = PDIDocument.GetTags();

	var container = document.createElement("div");
	document.insertBefore(container, null);
	container.innerHTML = IndexHTML;

	for ( var Elemid in container.all )
	{
		if ( Elemid != null && Elemid.search("index_field") >= 0 )
		{


			var Elem = document.getElementById(Elemid);

			var name = Elem.getAttribute("InternalName");
			if ( name == null || name == "" )
				name = Elem.id;

			var result_value = GetIndexFieldFromIA(RESCAN_GET_NODE_VALUE_PREFIX, id, name);

			if(result_value == null)
			{
				document.removeChild(container);
				return true;
			}
			if(result_value == "")
				continue;

			hasValue = true;
			index_fieldsXML += "<index_item>";
			index_fieldsXML += "<level>" + Level + "</level>";
			index_fieldsXML += "<name>" + escapeHTML(name) + "</name>";
			index_fieldsXML += "<id>" + Elem.id + "</id>";
			index_fieldsXML += "<value>" + escapeHTMLMultistring(result_value) + "</value>";
			index_fieldsXML += "</index_item>";
		}
	}


	index_fieldsXML += "</index_fields>";
	document.removeChild(container);

	if(!hasValue)
		return false;


	if ( Level == 0 )
	{
		if ( PDITags != null )
			PDITags.SetTag(AddStatusPrefix(EINPUT_PAGE_INDEX_FIELDS), index_fieldsXML );
	}
	else
		if ( DocPDITags != null )
			DocPDITags.SetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + id), index_fieldsXML );

	return false;
}

function GetIndexFieldFromIA(prefix, nodeId, name)
{
	g_curIAValue = null;

	getControlPanel().g_IsXMLValue = false;
	http_getiavalue.sendGetRequest(prefix + "&id=" + oIABatch.GetProcessID() + "&node=" + nodeId + "&name=" + name, GetIAValueCallback);
	ProcessLogout(http_getiavalue);

	return g_curIAValue;

}

function AsyncLoadInitialRescanData() {
	try {
		var FailedOnLoadIndexData = false;
		oPDIControl = getControlPanel().oPDIControl;

		if ( getControlPanel().g_CurrDocID != null ) {
			//oPDIControl.RefreshDocuments();
			var bDocumentExist = oPDIControl.LoadDocument( getControlPanel().g_CurrDocID );
			var PDIDocument = null;
			if ( bDocumentExist )
				PDIDocument = oPDIControl.GetDocument();

			if ( PDIDocument ) {
				var DocPDITags = PDIDocument.GetTags();

				if ( DocPDITags != null )
					DocPDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_LOAD_INDEX_DATA );

				var TreeXML = DocPDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE)) ;
				if ( TreeXML != null ) {
					var doc = GetXMLParser( TreeXML );
					if ( doc != null && doc.documentElement != null ) {
						var Tree = doc.documentElement;
						var nNodeCount = Tree.childNodes.length;
						//for each node
						for ( var j = 0; j < nNodeCount; j++ ) {
							var Node = Tree.childNodes(j);

							FailedOnLoadIndexData = LoadIndexFieldsForNode(Node, PDIDocument);

							if ( FailedOnLoadIndexData ) {
								if ( !confirm("Continue loading the initial indexing data?") )
									throw "Break";
							}
						}
					}
				}

				if ( !FailedOnLoadIndexData )
					DocPDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_READY );
			}
		}
	}
	catch(e) {
		if ( typeof(e) != 'string' )
			ShowErrorAlert("Error while downloading indexing fields data from the InputAccel Server", e.name, e.number, e.description, oPDIControl.GetContext(), true);

		alert("Batch was not successfully loaded from the eInput Server!\nTo continue loading the initial indexing data, reopen the batch in eIndex.");
	}

	CloseDownloadProgress();
	ShowBatchActions();

	CallAsyncresetHighlightedItem();

	CallDownloadImages();
}

function CallAsyncresetHighlightedItem()
{
    if ( getControlPanel().iAsyncresetHighlightedItem ) {
        getControlPanel().clearTimeout( getControlPanel().iAsyncresetHighlightedItem );
        getControlPanel().iAsyncresetHighlightedItem = null;
    }
    getControlPanel().iAsyncresetHighlightedItem = getControlPanel().setTimeout("getControlPanel().AsyncresetHighlightedItem()", 5);

}

function CallDownloadImages()
{
    if ( getControlPanel().oIABatch.GetIndexSettings().GetImageStreamingType() == 1 ) //LoadImagesInBackground:1
    {
        if ( getControlPanel().iDownLoadImages ) {
            getControlPanel().clearTimeout( getControlPanel().iDownLoadImages );
            getControlPanel().iDownLoadImages = null;
        }
        getControlPanel().iDownLoadImages = getControlPanel().setTimeout("getControlPanel().OnDownLoadImages()", 100);
    }

}

function AsyncresetHighlightedItem() {
	if ( getUIMode() == RESCAN_MODE )
	{
		if(!window.top.g_Module.FindAllRescanPages(
			getTreeViewPanel().oIATree,
			getControlPanel().oPDIControl))
				getTreeViewPanel().oIATree.resetHighlightedItem();

		FillRescanReason(0, 0);

	}
	else
	    getTreeViewPanel().oIATree.resetHighlightedItem();
}

function GetIAValueCallback() {
	if(!http_getiavalue.processHttpResponseXml("Unable to load the initial indexing data!"))
		return;

	g_curIAValue = "";
            
	try {

		if ( getControlPanel().g_IsXMLValue == true )
			g_curIAValue = http_getiavalue.getResponseResult().xml;
		else
			g_curIAValue = http_getiavalue.getResponseResult().text;
                
	}
	catch(e) {
		g_curIAValue = null;
		ShowErrorAlert("Error ", e.name, e.number, e.description, null, true);
		return;
	}
}

function OnDownloadAborted() {
    try {
        var oPDIControl = getControlPanel().oPDIControl;
        //oPDIControl.RefreshDocuments();
        
        if ( oPDIControl.LoadDocument( getControlPanel().g_CurrDocID ) ) {
            var PDIDoc = oPDIControl.GetDocument();
            var PDITags = PDIDoc.GetTags();
            PDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_CHECKEDOUT );
        }
    }
    catch(e) {
        alert(e.description);
    }
    CloseDownloadProgress();
    getControlPanel().ContinueRunAllBatches( true );
}

//----------------------------------------------------------------------------
//NewBatchRetFunc
//----------------------------------------------------------------------------


function InitializeGlobalVariables()
{
	oPDIControl = getControlPanel().oPDIControl;

	oIABatch = getControlPanel().oIABatch;

    if(getUIMode() != SCAN_MODE && g_CurrDocID == null && oPDIControl.GetDocument())
        	g_CurrDocID = oPDIControl.GetDocument().GetGUID();


	oModule = window.top.g_Module;

	oModule.setCurrentBatch(oIABatch);

	oModule.setPDIControl(oPDIControl);
}

function LoadLevels()
{
	if ( oIABatch.GetLevels() == null ||
		oIABatch.GetLevels() == "" ||
		oIABatch.GetLevels().GetLevelCount() == 0 ) {
		var doc = 
			getUIMode() == RESCAN_MODE ?
			GetXMLParser(DEFAULT_RESCAN_LEVELS_SETTINGS_XML) :
			GetXMLParser(DEFAULT_LEVELS_SETTINGS_XML);

		var root = doc.documentElement;
		oIABatch.LoadLevels(root);
	}

}

function SetPDITags(PDITags)
{
	if ( getUIMode() == SCAN_MODE ) {
		PDITags.SetTag(AddStatusPrefix(EINPUT_BATCHNAME), oIABatch.GetFullBatchName());
		PDITags.SetTag(AddStatusPrefix(EINPUT_BATCHNAME_ENTERED), oIABatch.GetBatchName());
		PDITags.SetTag(AddStatusPrefix(EINPUT_PROCESSNAME), oIABatch.GetProcessName());
	}

	PDITags.SetTag(AddStatusPrefix(EINPUT_PROCESSID), oIABatch.GetProcessID());
	PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_NAMINGSCHEMA), oIABatch.GetBatchNamingSchema());
	PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_PRIORITY), oIABatch.GetPriority());
	PDITags.SetTag(AddStatusPrefix(EINPUT_DESCRIPTION), oIABatch.GetDescription());
	PDITags.SetTag(AddStatusPrefix(EINPUT_ADVANCED_INDEXING_MODE), oIABatch.IsAdvancedIndexingMode() );

	PDITags.SetTag( AddStatusPrefix(EINPUT_BATCH_TYPE), getUIMode() );
	PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_LEVELS), oIABatch.GetLevels().SerializeToXML());

	PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_INDEXED), "false" );
	PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_RESCANNED), "false" );

	if ( getUIMode() == SCAN_MODE ||  getUIMode() == RESCAN_MODE ) {
		PDITags.SetTag(AddStatusPrefix(EINPUT_PRIVATE_BATCH), oIABatch.IsPrivateBatch());

		PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_FIELDS), oIABatch.GetScanIndexingSettings());
		PDITags.SetTag(AddStatusPrefix(EINPUT_SCANNER_SETTINGS), oIABatch.GetScannerSettingsXML());
		PDITags.SetTag(AddStatusPrefix(EINPUT_MISCELLANEOUS_SETTINGS), oIABatch.GetMiscellaneousSettingsXML());
		PDITags.SetTag(AddStatusPrefix(EINPUT_EVENT_ACTIONS), oIABatch.GetSeparationSettingsXML());

		PDITags.SetTag(AddStatusPrefix(EINPUT_SCANNED_SHEETS), 0);
		PDITags.SetTag(AddStatusPrefix(EINPUT_IMPORTED_FILES), 0);

	}
	if( getUIMode() == INDEX_MODE ||  getUIMode() == RESCAN_MODE ) {
		var commitResult = CommitIndexDoc();

		if ( commitResult != null && commitResult != false ) {
			PDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_COMMIT_STATE), 1 );
		}

	}

	if ( window.top.SESSION_ID == "" )
		window.top.SESSION_ID = GetCookie(EINPUT_SESSION_ID);

	PDITags.SetTag(AddStatusPrefix(EINPUT_SESSION_ID), window.top.SESSION_ID );

	var  strValue = PDITags.GetTag("PDI_TAG_CREATION_DATE_UTC");
	PDITags.SetTag(AddStatusPrefix(EINPUT_LEVEL7_CREATION_DATE), strValue);
	PDITags.SetTag(AddStatusPrefix(EINPUT_LEVEL7_MODIFICATION_DATE), strValue);

	if ( getUIMode() == INDEX_MODE || oIABatch.IsAdvancedIndexingMode() ) {
		PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_ZONES), oIABatch.GetZonesXML() );
		PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_FIELDS), oIABatch.GetIndexFieldsHTML() );
		PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_SETTINGS), oIABatch.GetIndexSettingsXML() );
		PDITags.SetTag(AddStatusPrefix(EINPUT_COMMIT_ALWAYS), oIABatch.GetIndexSettings().IsAllowCommitPartiallyIndexedTasks() );

		if ( getUIMode() == INDEX_MODE )
		{
			window.top.frames["IndexFieldsPanelFrame"].g_objValidator = new Validator();
			window.top.frames["IndexFieldsPanelFrame"].g_objValidator.Initialize(null);
			window.top.frames["IndexFieldsPanelFrame"].document.body.insertAdjacentHTML( "afterBegin", oIABatch.GetIndexFieldsHTML() );
		}
	}

}


function ApplayIndexUISettings()
{
	//Applay UI Settings
	if ( oIABatch.GetIndexSettings().IsAllowAnnotationToolbox() ) {
		window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoButtonsTbl").style.display = "block";
		//window.top.frames["ImageViewPanelFrame"].document.getElementById("PopulateWithOCR").style.display = "none";
		oPDIControl.SetAnnotationsMode(2);
		oPDIControl.EnableStickyMode();
	}
	else {
		window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoButtonsTbl").style.display = "none";
		oPDIControl.SetAnnotationsMode(1);
		oPDIControl.DisableStickyMode();
	}

	if ( oIABatch.GetIndexSettings().IsAllowRubberbandZone() ) {
		//window.top.frames["ImageViewPanelFrame"].document.getElementById("PopulateWithOCR").style.display = "inline";
		oPDIControl.SetAnnotationsMode(2);
		oPDIControl.EnableStickyMode();
		oPDIControl.SelectAnnotationTool(7); //Select Box
	}

	if ( !oIABatch.GetIndexSettings().IsAllowManipulateTree() ) {
		var Levels = oIABatch.GetLevels();
		var levelCount = Levels.GetLevelCount();
		for ( var i = 0; i < levelCount; i++ ) {
			var Level = Levels.GetLevel(i);
			Level.SetFlags( Level.IsDisplayAllowed() );
		}
	}
}

function LoadTreeScan(tree, PDITags)
{
	tree.SetAddAttachmentCallback("getControlPanel().OnAddAttachment");
	tree.SetViewAttachmentsCallback("getControlPanel().OnViewAttachments");
	tree.SetDeleteItemCallback("getControlPanel().OnDeleteTreeItem");
	tree.CreateTree( -1, 7 ); //create batch if it is visible

	if ( oIABatch.IsAdvancedIndexingMode() )
		PDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_LOAD_INDEX_DATA );

	UpdateUIForScan();

	return true;

}

function LoadTreeRescan(tree, PDITags)
{
	tree.SetAddAttachmentCallback("getControlPanel().OnAddAttachment");
	tree.SetViewAttachmentsCallback("getControlPanel().OnViewAttachments");
	tree.SetDeleteItemCallback("getControlPanel().OnDeleteTreeItem");
	tree.SetDownloadImageCallback("getControlPanel().OnDownloadImage");
	tree.saveHistory = true;

	UpdateUIForRescan(PDITags);

	var TreeXML = PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE)) ;
	if ( TreeXML == null || TreeXML == "" ) {
		if ( confirm("The selected batch is corrupt. Automatically repair the batch?") ) 
			RepairBatch( oIABatch, getTreeViewPanel().oIATree, PDITags );
		else {
			ClearAll();
			return false;
		}
	}

	tree.LoadTreeFromXML( TreeXML );
	tree.InitHistory(PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE_HISTORY)));
	tree.collapseAll();
	tree.expandAll();

	return true;
}

function LoadTreeIndex(tree, PDITags)
{
	tree.SetDownloadImageCallback("getControlPanel().OnDownloadImage");
	tree.multiSelectionIsEnabled = false;

	oPDIControl.SetThumbnailSize( "Standard", 1 );

	//Load batch tree from PDI document
	var TreeXML = PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE)) ;
	if ( TreeXML == null || TreeXML == "" ) {
		if ( confirm("The selected batch is corrupt. Automatically repair the batch?") ) {
			RepairBatch( oIABatch, getTreeViewPanel().oIATree, PDITags );
		}
		else {
			ClearAll();
			return false;
		}
	}

	tree.LoadTreeFromXML( TreeXML );
	tree.collapseAll();
	tree.expandAll();

	ApplayIndexUISettings();

	return true;
}

function LoadTree(PDITags)
{
	var tree = new IATree( oIABatch, oPDIControl );
	getTreeViewPanel().oIATree = tree;
	tree.SetHighlightItemCallback("getControlPanel().OnHighlightItem");
	tree.SetBeforeHighlightItem("getControlPanel().OnChangeCurrentPage");
	tree.setTreeId( "ia_tree2" );

	if ( getUIMode() == SCAN_MODE ) 
		return LoadTreeScan(tree, PDITags);
	else if ( getUIMode() == INDEX_MODE )
		return LoadTreeIndex(tree, PDITags);
	else if ( getUIMode() == RESCAN_MODE )
		return LoadTreeRescan(tree, PDITags);

	return false;

}

function LoadBatch(bInternal)
{
	try {
		if ( getUIMode() == SCAN_MODE )
			window.top.document.title = "eScan - " + oIABatch.GetFullBatchName();

		var PDIDoc = oPDIControl.GetDocument();
		var PDITags = PDIDoc.GetTags();

		ClearAll( bInternal );

		SetPDITags(PDITags);

		if(!LoadTree(PDITags))
			return false;

		return true;

	}
	catch(e) {
		ShowErrorAlert("Error while creating the batch", e.name, e.number, e.description, oPDIControl.GetContext(), true);
		getControlPanel().g_CurrDocID = null;
	}

	return false;

}

function CheckClosing()
{
	while(window.top.isDownloading)
	{
		alert("Closing of child windows. Press OK when Send/Receive windows will be closed");
	}
}

function OnExit()
{
	if(window.top.isDownloading)
	{
		alert("Could not close the module while batch is being sent");
		return;
	}
	window.top.close();
}

function NewBatchRetFunc( NewIABatch, bInternal ) {
    try {
		window.top.isDownloading = false;
	    getControlPanel().ShowWaitCursor();

		InitializeGlobalVariables();

        oPDIControl.Log("[BEGIN] Create new batch");

        var bDocumentExist = oModule.OpenDocument(NewIABatch);
        
		oIABatch = oModule.currentBatch;

        if ( bDocumentExist ) {
			LoadLevels();

			InitializeScanTab();
            
            if ( oIABatch.BuildBatchNameFromSchema() )
				 if(!LoadBatch(bInternal))
					 return;
      
        }
        else {
            EnableScanControls(null, false);
        //TODO: Disable other controls
        }
        

        if ( getUIMode() != INDEX_MODE && oPDIControl != null ) {
            if ( bInternal != true )
                oPDIControl.Show();
            
            ShowBatchActions();
        }

    }
    catch(e) {
        ShowErrorAlert("Error while creating the batch", e.name, e.number, e.description, oPDIControl.GetContext(), true);
        getControlPanel().g_CurrDocID = null;
    }  
    
    // Fix ESCAN-1327
    //if ( getUIMode() == INDEX_MODE )
    //    TerminateValidation();
    
    if ( oPDIControl != null )
        oPDIControl.Log("[END] Create new batch");

	oPDIControl.GetDocument().SetRuntimeMarkup();

    if ( getUIMode() == INDEX_MODE && bDocumentExist) {
        var PDIDoc = oPDIControl.GetDocument();
        var PDITags = PDIDoc.GetTags();
        PDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_STATUS), BATCH_STATUS_DOWNLOADED );
    }
    
    ShowAutoCursor();
    
    if ( getUIMode() == INDEX_MODE ) {
        OnLoadInitialIndexData();
    }
    if ( getUIMode() == RESCAN_MODE ) {
        OnLoadInitialRescanData();
    }
    if ( getUIMode() == SCAN_MODE ) {
		oPDIControl.GetDocument().Flush(false);
	}

    GetModuleHelper().GetControlHelper().CallBatchCreated();
}

//----------------------------------------------------------------------------
//End of NewBatchRetFunc
//----------------------------------------------------------------------------

function RepairBatch( oIABatch, oIATree, oPDITags ) {
    
}

//----------------------------------------------------------------------------
//OpenBatchRetFunc
//----------------------------------------------------------------------------

function loadUnloadedBatch(PDITags, status)
{
	if ( window.top.AdvancedIndexingDocID != "" )
		oIABatch.SetAdvancedIndexingMode("true");

	//Check statuses
	var CheckFailed = false;
	var IsCommit = Number( PDITags.GetTag(AddStatusPrefix(EINDEX_BATCH_COMMIT_STATE)) );

	status = Number( PDITags.GetTag(AddStatusPrefix(EINDEX_BATCH_STATUS)) );
	if ( !oIABatch.IsAdvancedIndexingMode() && !IsCommit ) {
		var commitResult = CommitIndexDoc();

		if ( commitResult == null ) {
			alert("The eInput Server did not commit this batch so it cannot be opened completely.");
			CheckFailed = true;
		}
		else if ( commitResult == false ) {
			alert("This batch was removed from the eInput Server and cannot be opened.");
			CheckFailed = true;
		}
		else {
			//all Ok!
			PDITags.SetTag(AddStatusPrefix(EINDEX_BATCH_COMMIT_STATE), 1 );
		}
	}

	if ( CheckFailed ) {
		OnCancelBatch();
		oPDIControl.Show();
		return false;
	}
	else {
		if ( status <= BATCH_STATUS_DOWNLOADED ) {
			OnCancelBatch();

			//oIABatch = getControlPanel().oIAProcesses.GetByProcessName( PDITags.GetTag(AddStatusPrefix(EINPUT_PROCESSNAME) ) );
			if ( oIABatch != null ) {
				OpenDownloadProgress( "Continue Downloading Batch...", "ProgressDlg");

				if ( status < BATCH_STATUS_DOWNLOADED ) {
					var PDIDocument = oPDIControl.GetDocument();
					getControlPanel().g_CurrDocID = PDIDocument.GetGUID();
					DownloadBatch();
					return false;
				}
				else {
					OnDownloadFinished();
					return false;
				}
			}
			else {
				alert("Unable to continue loading the specified batch.\nThe corresponding InputAccel process was not found.");
			}
		}
	}
	return true;
// Fix ESCAN-1327
//TerminateValidation();
}

function LoadZonesSettings(PDITags)
{
	if ( getUIMode() == INDEX_MODE || oIABatch.IsAdvancedIndexingMode() ) {
		var ZonesXML = "";
		oPDIControl.Log("[BEGIN] Load zones settings");
		try {
			ZonesXML = PDITags.GetTag(AddStatusPrefix(EINPUT_INDEX_ZONES));
		}
		catch(e)
		{}

		if ( ZonesXML != null && ZonesXML != "" )
			oIABatch.LoadZones( ZonesXML )

		oPDIControl.Log("[END]");

		oPDIControl.Log("[BEGIN] Index fields HTML");
		var IndexFieldsHTML = "";
		try {
			IndexFieldsHTML = PDITags.GetTag(AddStatusPrefix(EINPUT_INDEX_FIELDS));
		}
		catch(e)
		{}

		if ( IndexFieldsHTML != null && IndexFieldsHTML != "" )
			oIABatch.SetIndexFieldsHTML( IndexFieldsHTML );

		oPDIControl.Log("[END]");

		oPDIControl.Log("[BEGIN] Load index configuration");
		var IndexConfiguration = "";
		try {
			IndexConfiguration = PDITags.GetTag(AddStatusPrefix(EINPUT_INDEX_SETTINGS));
		}
		catch(e)
		{}

		if ( IndexConfiguration != null && IndexConfiguration != "" )
			oIABatch.LoadIndexSettings( IndexConfiguration );

		oPDIControl.Log("[END]");
	}
}

function LoadIndexingFields(PDITags)
{
	if ( getUIMode() == SCAN_MODE || getUIMode() == RESCAN_MODE ) {
		if ( !oIABatch.IsAdvancedIndexingMode() ) {
			oPDIControl.Log("[BEGIN] Load index fields");
			var IndexSettingsXML = "";

			try {
				IndexSettingsXML = PDITags.GetTag(AddStatusPrefix(EINPUT_INDEX_FIELDS));
			}
			catch(e)
			{}

			if ( IndexSettingsXML != null && IndexSettingsXML != "" )
				oIABatch.LoadScanIndexingSettings( IndexSettingsXML );

			oPDIControl.Log("[END]");
		}

		oPDIControl.Log("[BEGIN] Load scanner settings");
		var ScannerSettingsXml = "";

		try {
			ScannerSettingsXml = PDITags.GetTag(AddStatusPrefix(EINPUT_SCANNER_SETTINGS));
		}
		catch(e)
		{}

		if ( ScannerSettingsXml != null && ScannerSettingsXml != "" )
			oIABatch.LoadScannerSettings2( ScannerSettingsXml );


		oPDIControl.Log("[END]");

		oPDIControl.Log("[BEGIN] Load miscellaneous settings");
		var MiscellaneousSettingsXML = "";

		try {
			MiscellaneousSettingsXML = PDITags.GetTag(AddStatusPrefix(EINPUT_MISCELLANEOUS_SETTINGS));
		}
		catch(e)
		{}

		if ( MiscellaneousSettingsXML != null && MiscellaneousSettingsXML != "" )
			oIABatch.LoadMiscellaneousSettings( MiscellaneousSettingsXML );

		oPDIControl.Log("[END]");

		oPDIControl.Log("[BEGIN] Load event actions settings");
		var EventActionsXML = "";

		try {
			EventActionsXML = PDITags.GetTag(AddStatusPrefix(EINPUT_EVENT_ACTIONS));
		}
		catch(e)
		{}

		if ( EventActionsXML != null && EventActionsXML != "" )
			oIABatch.LoadSeparationSettings( EventActionsXML );

		oPDIControl.Log("[END]");
	}
	
}

function CheckVersion(PDITags)
{
	//TODO: refactoring
	try {
		var Version = PDITags.GetTag(AddStatusPrefix(EINPUT_PROCESSID) );
	}
	catch(e) {
		if ( confirm("The selected batch is based on an old format.\n Automatically convert the batch to " + EINPUT_VERSION_VALUE + " version format?") )
			RepairBatch( oIABatch, getTreeViewPanel().oIATree, PDITags );
		else
			ClearAll();
		return false;
	}
	return true;
}

function UpdateUIForIndex(PDITags)
{
		oPDIControl.Log("[BEGIN] Load HTML content");
		window.top.frames["IndexFieldsPanelFrame"].document.body.insertAdjacentHTML( "afterBegin", oIABatch.GetIndexFieldsHTML() );
		oPDIControl.Log("[END]");

		if ( oIABatch.IsAdvancedIndexingMode() ) {
			getControlPanelEl("tdSending").style.display = "none";
			getControlPanelEl("tdSaveBatch").style.display = "none";
			getControlPanelEl("tdCloseAdvancedIndex").style.display = "inline";
			getControlPanelEl("tdCloseBatch").style.display = "none";

		}
		else {
			getControlPanelEl("tdSending").style.display = "inline";
			getControlPanelEl("tdSaveBatch").style.display = "inline";
			getControlPanelEl("tdCloseAdvancedIndex").style.display = "none";
			getControlPanelEl("tdCloseBatch").style.display = "inline";
		}
		
		ApplayIndexUISettings();

		oPDIControl.SetThumbnailSize( "Standard", 1 );


		var IsIndexed = ( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_INDEXED)) == "true" ? true : false );
		getControlPanelEl("btnAcceptTask").disabled = IsIndexed;

		if ( oIABatch.GetIndexSettings().GetImageStreamingType() == 1 ) //LoadImagesInBackground:1
		{
			if ( getControlPanel().iDownLoadImages ) {
				getControlPanel().clearTimeout( getControlPanel().iDownLoadImages );
				getControlPanel().iDownLoadImages = null;
			}
			getControlPanel().iDownLoadImages = getControlPanel().setTimeout("getControlPanel().OnDownLoadImages()", 100);
		}
}

function UpdateUIForScan()
{

	//Hardcoded 1 - JPEG
	oPDIControl.SetThumbnailSize( oIABatch.GetMiscellaneousSettings().GetThumbnailSize(), 1 );

	if ( oIABatch.IsAdvancedIndexingMode() ) {
		getControlPanelEl("sc03").style.display = "none";
		getControlPanelEl("tdAdvancedIndexing").style.display = "inline";
	}
	else {
		getControlPanelEl("tdAdvancedIndexing").style.display = "none";
		getControlPanelEl("sc03").style.display = "inline";
	}

	getControlPanelEl("btnAdvancedIndexing").disabled = true;

}

function UpdateUIForRescan(PDITags)
{
	oPDIControl.SetThumbnailSize( oIABatch.GetMiscellaneousSettings().GetThumbnailSize(), 1 );

	getControlPanelEl("tdAdvancedIndexing").style.display = "none";
	getControlPanelEl("sc03").style.display = "inline";
	getControlPanelEl("sc04").style.display = "inline";
	getControlPanelEl("btnAdvancedIndexing").disabled = true;

	oPDIControl.GetDocument();

	var IsIndexed = ( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_RESCANNED)) == "true" ? true : false );
	getControlPanelEl("btnNextRescanPages").disabled = IsIndexed;

	expandcontent('sc4', document.getElementById('sc04'));
}

function UpdateUI(PDITags)
{
	if ( getUIMode() == INDEX_MODE )
		UpdateUIForIndex(PDITags);

	else if ( getUIMode() == SCAN_MODE )
		UpdateUIForScan();
	
	else if ( getUIMode() == RESCAN_MODE ) 
		UpdateUIForRescan(PDITags);


}

function CustomizeTreeView(TreeXML, PDITags)
{
	oPDIControl.Log("[BEGIN] Initialize tree view control");
	getTreeViewPanel().oIATree = new IATree( oIABatch, oPDIControl );
	getTreeViewPanel().oIATree.setTreeId( "ia_tree2" );
	getTreeViewPanel().oIATree.SetHighlightItemCallback("getControlPanel().OnHighlightItem");

	if ( getUIMode() == SCAN_MODE ||  getUIMode() == RESCAN_MODE ) {
		getTreeViewPanel().oIATree.SetAddAttachmentCallback("getControlPanel().OnAddAttachment");
		getTreeViewPanel().oIATree.SetViewAttachmentsCallback("getControlPanel().OnViewAttachments");
		getTreeViewPanel().oIATree.SetDeleteItemCallback("getControlPanel().OnDeleteTreeItem");
	}
	else if ( getUIMode() == INDEX_MODE ) {
		getTreeViewPanel().oIATree.SetDownloadImageCallback("getControlPanel().OnDownloadImage");
		getTreeViewPanel().oIATree.multiSelectionIsEnabled = false;
		getTreeViewPanel().oIATree.saveHistory = true;

	}

	getTreeViewPanel().oIATree.SetBeforeHighlightItem("getControlPanel().OnChangeCurrentPage");
	getTreeViewPanel().oIATree.LoadTreeFromXML( TreeXML );
	getTreeViewPanel().oIATree.InitHistory(PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE_HISTORY)));
	oPDIControl.Log("[END]");

	getTreeViewPanel().oIATree.collapseAll();
	getTreeViewPanel().oIATree.expandAll();

	if ( getUIMode() == SCAN_MODE )
		getControlPanelEl("btnAdvancedIndexing").disabled = getTreeViewPanel().oIATree.GetPageCount() <= 0;

}

function LoadTree2(PDITags)
{
	var TreeXML;
	try {
		oIABatch.SetProcessID( PDITags.GetTag(AddStatusPrefix(EINPUT_PROCESSID) ));
		oIABatch.SetBatchNamingSchema( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_NAMINGSCHEMA)));
		oIABatch.SetPriority( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_PRIORITY)));
		oIABatch.SetDescription( PDITags.GetTag(AddStatusPrefix(EINPUT_DESCRIPTION)));


		oIABatch.LoadLevels( GetXMLParser( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_LEVELS)) ).documentElement );

		InitializeScanTab();

		TreeXML = PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_TREE)) ;

		//set index fields
		UpdateUI(PDITags);

	}
	catch(e) {
		if ( confirm("The selected batch is corrupt."
			+e.message+
			"Automatically repair this batch?") )
			RepairBatch( oIABatch, getTreeViewPanel().oIATree, PDITags );
		else
			ClearAll();
		return false;
	}

	if ( TreeXML == null || TreeXML == "" ) {
		if ( confirm("The selected batch is corrupt. Automatically repair this batch?") ) {
			RepairBatch( oIABatch, getTreeViewPanel().oIATree, PDITags );
		}
		else {
			ClearAll();
			return false;
		}
	}

	 CustomizeTreeView(TreeXML, PDITags);

	 return true;

}

//loads previously saved document
function LoadBatch2()
{
	var PDIDocument = oPDIControl.GetDocument();
	var PDITags = PDIDocument.GetTags();
	var status = null;

	oIABatch.SetFullBatchName( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCHNAME)) );
	oIABatch.SetBatchName( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCHNAME_ENTERED)) );
	oIABatch.SetProcessID( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCHID) ));
	oIABatch.SetProcessName( PDITags.GetTag(AddStatusPrefix(EINPUT_PROCESSNAME) ));
	oIABatch.SetInstance( PDITags.GetTag( INSTANCE ) );

	oIABatch.SetAdvancedIndexingMode( PDITags.GetTag(AddStatusPrefix(EINPUT_ADVANCED_INDEXING_MODE)) );
	//window.top.SESSION_ID = PDITags.GetTag(AddStatusPrefix(EINPUT_SESSION_ID));


	if ( getUIMode() == INDEX_MODE || getUIMode() == RESCAN_MODE) {
		if(!loadUnloadedBatch(PDITags, status))
			return false;
	}

	LoadZonesSettings(PDITags);

	LoadIndexingFields(PDITags);

	if(!CheckVersion(PDITags))
		return false;
	
	if(!LoadTree2(PDITags))
		return false;

	if ( getUIMode() == INDEX_MODE ) {
		if ( oIABatch.IsAdvancedIndexingMode() )
			window.top.document.title = "eScan (Advanced Indexing) - " + oIABatch.GetFullBatchName();
		else
			window.top.document.title = "eIndex - " + oIABatch.GetFullBatchName();
	}
	else {
		window.top.document.title = "eScan - " + oIABatch.GetProcessName();
	}

	return true;
//var strValue = PDITags.GetTag("PDI_TAG_MODIFICATION_DATE");

}

function OpenBatchRetFunc( SelectedBatch ) {
    try {
        UpdateProfiles();
		getControlPanel().ShowWaitCursor();
        
        //oPDIControl.RefreshDocuments();

        oPDIControl.Log("[BEGIN] Open batch");
        
        if ( SelectedBatch.length ) {
            var arrIds = SelectedBatch[0].split("|");
            
            if ( arrIds.length ) {
                if ( oPDIControl.LoadDocument( arrIds[2] ))//load by GUID
                {
                    getControlPanel().oIABatch = new IABatch( window.top.g_CachedMode, getUIMode() );

					InitializeGlobalVariables();

					if(!LoadBatch2()){
					    ShowAutoCursor();
						return;
					}
                //PDITags.SetTag(AddStatusPrefix("ModificationDate"), MakeUTC(strValue));
                    ShowBatchActions()
                }
                else {
                    ShowAutoCursor();
                    return;
                }
            }
        }	
    }
    catch(e) {
        ShowErrorAlert("Error while opening the batch", e.name, e.number, e.description, oPDIControl != null ? oPDIControl.GetContext() : null, true);
        if ( getTreeViewPanel().oIATree != null )
            getTreeViewPanel().oIATree.ClearSelection();
        OnCancelBatch();
        ShowAutoCursor();
        return;
    }
	
	oPDIControl.GetDocument().SetRuntimeMarkup();
    
    oPDIControl.Log("[END] Batch opened");
    
    if ( getUIMode() == INDEX_MODE && status == BATCH_STATUS_LOAD_INDEX_DATA ) {
        if ( getControlPanel().oIABatch.IsAdvancedIndexingMode() || confirm("Batch was not successfully loaded from the eInput Server!\nContinue loading the initial indexing data?") ) {
            var PDIDocument = oPDIControl.GetDocument();
            getControlPanel().g_CurrDocID = PDIDocument.GetGUID();
            OpenDownloadProgress( "Continue Downloading Batch...", "ProgressDlg");
            OnLoadInitialIndexData();
            return;
        }
    }
    
    if ( oPDIControl != null )
        oPDIControl.Show();
    
    if ( getUIMode() == INDEX_MODE )
        window.top.frames["IndexFieldsPanelFrame"].document.body.style.visibility = "visible";

	CallAsyncresetHighlightedItem();
    
    ShowAutoCursor();
}

//----------------------------------------------------------------------------
//End of OpenBatchRetFunc
//----------------------------------------------------------------------------


function LoginRetFunc( retval ) {
    //Work offline...
    if ( !navigator.onLine ) {
        window.top.SESSION_ID = GetCookie(EINPUT_SESSION_ID);
    }   
    else {
        if ( retval == OFFLINE )
            window.top.g_CachedMode = true;
    }
	
    if ( getControlPanel().iOfflineModeCheck == null )
        getControlPanel().iOfflineModeCheck = getControlPanel().setInterval("getControlPanel().OnCheckOffline()", 500);
    
    ShowAutoCursor();
    
    if ( window.top.SESSION_ID == "" && retval == OFFLINE ) {
        if ( getUIMode() == SCAN_MODE ) {
            getControlPanelEl("btnNewBatch").disabled = true;
            getControlPanelEl("btnOpenBatch").disabled = true;
            alert("There is no offline content available.\nConnect to the InputAccel Server before continuing.");
        }
        else if ( getUIMode() == INDEX_MODE ) {
            if ( window.top.g_IsOpened ) //if opened from external browser
            {
                if ( window.top.g_ArgDocID != null ) {
                    var arSelectedBatches = new Array();
                    arSelectedBatches[0] = "||" + window.top.g_ArgDocID;
                    getControlPanel().OpenBatchRetFunc( arSelectedBatches );
                    window.top.g_ArgDocID = null;
                }
                
                window.top.g_IsOpened = false;
            }
        }
    }
    else {    
        getControlPanel().LoadProcesses( window.top.g_CachedMode );
        
        if ( window.top.AdvancedIndexingDocID != null && window.top.AdvancedIndexingDocID != "" ) {
            var arSelectedBatches = new Array();
            arSelectedBatches[0] = "||" + window.top.AdvancedIndexingDocID;
            getControlPanel().OpenBatchRetFunc( arSelectedBatches );
        }
        else {
            if ( window.top.g_IsOpened ) //if opened from external browser
            {
                window.top.opener.SESSION_ID = window.top.SESSION_ID;
                if ( window.top.g_ArgDocID != null ) {
                    if ( getUIMode() == SCAN_MODE && window.top.g_ArgDocID == "NewBatch") {
                        getControlPanelEl("btnNewBatch").click();
                    }
                    else {
                        var arSelectedBatches = new Array();
                        arSelectedBatches[0] = "||" + window.top.g_ArgDocID;
                        getControlPanel().OpenBatchRetFunc( arSelectedBatches );
                        window.top.g_ArgDocID = null;
                    }
                }
                
                window.top.g_IsOpened = false;
            }
        }
    }

    GetModuleHelper().GetControlHelper().LoginFinished();
}

function OnDeletePage() {
    getTreeViewPanel().oIATree.deleteItem(
        null,
        getTreeViewPanel().oIATree.GetSelectedItems() );

//TODO: Sync view options
//TODO: Sync tree view and indexes
}

function OnFirstPage() {
    getTreeViewPanel().oIATree.highlightFirstItem();
}

function OnPrevPage() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 0, false );
}

function OnSelectPrevPage() {
	getTreeViewPanel().oIATree.ctrlKey = true;
    getTreeViewPanel().oIATree.highlightItemByLevel( 0, false );
	getTreeViewPanel().oIATree.ctrlKey = false;
}

function OnSelectNextPage() {
	getTreeViewPanel().oIATree.ctrlKey = true;
    getTreeViewPanel().oIATree.highlightItemByLevel( 0, true );
	getTreeViewPanel().oIATree.ctrlKey = false;
}

function OnSelectToLastPage() {
	getTreeViewPanel().oIATree.shiftKey = true;
    getTreeViewPanel().oIATree.highlightLastItem();
	getTreeViewPanel().oIATree.shiftKey = false;
}

function OnSelectToFirstPage() {
	getTreeViewPanel().oIATree.shiftKey = true;
    getTreeViewPanel().oIATree.highlightFirstItem();
	getTreeViewPanel().oIATree.shiftKey = false;
}

function OnNextPage() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 0, true );
}

function OnLastPage() {
    getTreeViewPanel().oIATree.highlightLastItem();
}

function OnGoToPage() {
    try {
        var nImgCount = oPDIControl.GetImagesCount();
        var promptMsg = "Enter page number [1 - " + nImgCount + "]";
        
        var replay = prompt( String(promptMsg), "1" );
        
        if ( replay != "" && replay != null ) {
            var numReplay = Number(replay);
            if ( numReplay >= 1 && numReplay <= nImgCount  ) {
                getTreeViewPanel().oIATree.highlightItemByIndex( numReplay - 1 );
            }
            else {
                alert("Invalid Page number specified!");
            }
        }
    }
    catch ( e ) {
        ShowErrorAlert("Error occurred", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }
}

//Advanced navigation

function OnNextLevel1Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 1, true );
}

function OnPreviousLevel1Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 1, false );
}

function OnNextLevel2Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 2, true );
}

function OnPreviousLevel2Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 2, false );
}

function OnNextLevel3Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 3, true );
}

function OnPreviousLevel3Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 3, false );
}

function OnNextLevel4Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 4, true );
}

function OnPreviousLevel4Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 4, false );
}

function OnNextLevel5Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 5, true );
}

function OnPreviousLevel5Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 5, false );
}

function OnNextLevel6Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 6, true );
}

function OnPreviousLevel6Node() {
    getTreeViewPanel().oIATree.highlightItemByLevel( 6, false );
}

function ClearIndexFieldsTab()
{
    getControlPanelEl("sc3").innerHTML = "No indexing fields";
}

function ClearRescanReasonTab()
{
    getControlPanelEl("sc4").innerHTML = "";
}

//////////////////////////////
function ClearAll(bSkipProgressClosing) {
    ShowAutoCursor();
    
    oPDIControl.ClearView();
    oPDIControl.Orientation(0);
	ClearIndexFieldsTab();
	ClearRescanReasonTab();

	if ( getTreeViewPanel().oIATree != null )
        getTreeViewPanel().oIATree.UnInitialize();
    
    getTreeViewPanel().document.getElementById("ia_tree2").innerHTML = "";
    
    if ( bSkipProgressClosing != true )
        getControlPanelEl( "ProgressDlg" ).setAttribute("closed", true);
    
    if ( getUIMode() == INDEX_MODE ) {
        //oPDIControl.HideOCRZones();
        
        var oValidator = window.top.frames["IndexFieldsPanelFrame"].g_objValidator;
        if ( oValidator != null )
            oValidator.DetachAll();
        
        window.top.frames["IndexFieldsPanelFrame"].document.body.innerHTML = "";
    }
}

function FillNewBatchControls( ContentID ) {
    UpdateProfiles();
    if ( oIAProcesses.Update( window.top.g_CachedMode ) ) {
        oIAProcesses.Load( window.top.g_CachedMode );
        
        var oNewBatchDlgContent = document.getElementById(ContentID);	
        var selProcessList = oNewBatchDlgContent.document.getElementById("Process");
        var nCount = oIAProcesses.Count();
        
        if ( nCount )
            selProcessList.innerHTML = "";
        
        for ( var i = 0; i < nCount; i++ ) {
            var oOption = document.createElement("OPTION");
            selProcessList.options.add(oOption);
            selProcessList.style.visibility = "visible";
            oOption.innerText = oIAProcesses.Item(i).GetProcessName();
            oOption.value = oIAProcesses.Item(i).GetProcessID();
            
            if ( getControlPanel().g_iLastUsedProcessID != null &&
                getControlPanel().g_iLastUsedProcessID == oOption.value ) {
                selProcessList.selectedIndex = i;
            }
        }
        if ( nCount ) {
            if ( selProcessList.selectedIndex == -1 )
                selProcessList.selectedIndex = 0;
            
            OnSelectProcess( selProcessList );
        }
        return true;
    }
    return false;
//oIAProcesses.Update(window.top.g_CachedMode);
}

function OnUseDefPriority() {
    var checked = document.getElementById('chkUseDefPriority').checked;
    
    document.getElementById('BatchPriority').disabled = checked;
    if (!checked) {
        document.getElementById('BatchPriority').focus();
    }
    else {
        var oProcesses = getControlPanel().oIAProcesses;
        var ProcessList = document.getElementById("Process");
        if ( ProcessList != null && oProcesses != null && ProcessList.selectedIndex != -1 ) {
            var ProcessID = ProcessList.options[ProcessList.selectedIndex].value;
            var selItem = oProcesses.GetByID( ProcessID );
            if ( selItem != null )
                document.getElementById('BatchPriority').value = selItem.GetPriority();
        }
    }
}

function OnSelectProcess(ProcessList) {
    var oProcesses = getControlPanel().oIAProcesses;
    var ProcessID = ProcessList.options[ProcessList.selectedIndex].value;
    getControlPanel().g_iLastUsedProcessID = ProcessID;
    
    var selItem = oProcesses.GetByID( ProcessID );
    
    ProcessList.document.getElementById("BatchNameSchema").value = selItem.GetBatchNamingSchema();
    if ( ProcessList.document.getElementById("BatchNameSchema").value.replace( / /g, "" ) == "" ) {
        ProcessList.document.getElementById("tdNameSchema").style.visibility = "hidden";
        ProcessList.document.getElementById("BatchNameSchema").style.visibility = "hidden";
    }
    else {
        ProcessList.document.getElementById("tdNameSchema").style.visibility = "visible";
        ProcessList.document.getElementById("BatchNameSchema").style.visibility = "visible";
    }
    
    if ( selItem.GetMiscellaneousSettings().IsPrioritySelectionDisabled() ) {
        ProcessList.document.getElementById("chkUseDefPriority").style.display = "none";
        ProcessList.document.getElementById("BatchPriority").style.display = "none";
        ProcessList.document.getElementById("BatchPrioritySpan").style.display = "none";
        ProcessList.document.getElementById("tdUseDefPriority").style.display = "none";
        ProcessList.document.getElementById("BatchPriority").value = selItem.GetPriority();
    }
    else {
        ProcessList.document.getElementById("chkUseDefPriority").style.display = "inline";
        ProcessList.document.getElementById("BatchPriority").style.display = "inline";
        ProcessList.document.getElementById("BatchPrioritySpan").style.display = "inline";
        ProcessList.document.getElementById("tdUseDefPriority").style.display = "inline";
        if ( ProcessList.document.getElementById("chkUseDefPriority").checked )
            ProcessList.document.getElementById("BatchPriority").value = selItem.GetPriority();
    }
    
    if ( selItem.GetMiscellaneousSettings().IsDescriptionSelectionDisabled() ) {
        ProcessList.document.getElementById("Description").style.display = "none";
        ProcessList.document.getElementById("tdDescription").style.display = "none";
        
        ProcessList.document.getElementById("Description").innerText = selItem.GetDescription();
    }
    else {
        ProcessList.document.getElementById("Description").style.display = "inline";
        ProcessList.document.getElementById("tdDescription").style.display = "inline";
        
        if ( ProcessList.document.getElementById("Description").getAttribute("IsModified") != "true")
            ProcessList.document.getElementById("Description").innerText = selItem.GetDescription();
    }   
    
    ProcessList.document.getElementById("BatchNameSchema").disabled = !selItem.GetMiscellaneousSettings().IsBatchSchemaSelectionAllowed();
    ProcessList.document.getElementById("PrivateBatch").checked = selItem.GetMiscellaneousSettings().IsPrivateBatch();
    
    ProcessList.document.getElementById("BatchName").value = "";
    var noNameTag = ( ProcessList.document.getElementById("BatchNameSchema").value.search( /@\(Name\)/ ) == -1 );
    if ( ProcessList.document.getElementById("BatchNameSchema").value == "" )
        noNameTag = false;
    ProcessList.document.getElementById("BatchName").disabled = noNameTag;
    ProcessList.document.getElementById("tdBatchName").disabled = noNameTag;
}

function BatchDesc(strBatchName, ProcessName, DocId)
{
	this.strBatchName = strBatchName;
	this.ProcessName = ProcessName;
	this.DocId = DocId;
}

function sortedInsert(processes, batch)
{
	var n  = processes.length;
	var i = 0;

	while(i < n && processes[i].strBatchName <= batch.strBatchName)
		i++;

	var k = i;

	for(i=n;i > k;i--)
		processes[i] = processes[i-1];

	processes[k] = batch;
}


function LoadLocallySavedBatches() {
    //getControlPanel().oPDIControl.RefreshDocuments();
    
    var PDIDocuments = getControlPanel().oPDIControl.GetDocuments();
    var nDocuments   = PDIDocuments.GetCount();
	var batches = new Array();
    
    var BatchesList = "";
    
    for (i = 0; i < nDocuments; i++) {
        var PDIDocument = PDIDocuments.GetByIndex(i);
        var PDITags = PDIDocument.GetTags();
		if(PDIDocument.PDIDocument.IsRuntimeFlagSet())
			continue;
        
        try {
            var TagState = PDITags.PDIGetTag("PDI_TAG_STATE").toLowerCase();
            var Type = PDITags.PDIGetTag(AddStatusPrefix(EINPUT_BATCH_TYPE));
            
            var bAuxCheck = true;
            
            if ( getUIMode() == INDEX_MODE && g_bIsIndexed != null ) {
                var IsIndexed = ( PDITags.PDIGetTag(AddStatusPrefix(EINPUT_BATCH_INDEXED)) == "true" ? true : false );
                bAuxCheck = (IsIndexed == g_bIsIndexed);
            }

            if ( getUIMode() == RESCAN_MODE && g_bIsIndexed != null ) {
                var IsRescanned = ( PDITags.PDIGetTag(AddStatusPrefix(EINPUT_BATCH_RESCANNED)) == "true" ? true : false );
                bAuxCheck = (IsRescanned == g_bIsIndexed);
            }

            if ( TagState != "sent" && TagState != "sending" && TagState != "sending paused" && TagState != "downloading" && Type.toLowerCase() == getUIMode().toLowerCase() && bAuxCheck ) {
                var strBatchName = escapeHTML( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCHNAME)) );
                var ProcessName = escape(escapeHTML(PDITags.GetTag(AddStatusPrefix(EINPUT_PROCESSNAME))));
                
                var DocId = PDIDocument.GetGUID();

				sortedInsert(batches, new BatchDesc(strBatchName, ProcessName, DocId));

				nDocCount++;
            }
        }
        catch(e) {
        }
    }

	for(i = 0; i < batches.length; i++)
	{
		BatchesList += "<option value='" + escape(batches[i].strBatchName) + "|" + batches[i].ProcessName + "|" + batches[i].DocId + "'>";
		BatchesList += batches[i].strBatchName;
		BatchesList += "</option>";
	}
	
    return BatchesList;
}

function UpdateProfiles(){
    var profiles =
        getControlPanel().oIAProfiles = new IAScanProfiles(window.top.g_CachedMode);

    if(!profiles.DoRequest(true))
        return false;
    profiles.Load();
    return true;
}

function LoadBatches( IsMultiple, LocalBatchesOnly, silent ) {
	nDocCount = -1;
    if ( !LocalBatchesOnly ) {
        UpdateProfiles();
        
        if ( getControlPanel().oIAProcesses.Update( window.top.g_CachedMode, silent ) )
            getControlPanel().oIAProcesses.Load( window.top.g_CachedMode );
        else
            return false;
    }
    
    //Load PDI documents
    nDocCount = 0;
    
    try {
        var oOpenBatchDlgContent = getControlPanelEl("OpenBatchDlgContent");
        
        var BatchesList = "";
        
        if ( (getUIMode() == INDEX_MODE ||  getUIMode() == RESCAN_MODE)
			&& !LocalBatchesOnly ) //Advanced loading options
        {
            BatchesList += "<SELECT IsPopupControl='true' id='BatchesListOptions' " + ( !navigator.onLine ? "DISABLED" : "" ) + " style='width:100%' onchange='OnSelectBatchesListOptions(this)' >";
            BatchesList += "<option value=0>Show locally saved batches</option>";
            BatchesList += "<option selected value=1>Show batches available for download</option>";
            BatchesList += "</SELECT><BR><BR>";
            nDocCount   += getControlPanel().oIAProcesses.Count();
        }
        
        BatchesList += "<SELECT ACCESSKEY='b' IsPopupControl='true' ondblclick='OnOpenBatchDlgClose()' id='batches' style='width:100%' size='20'>";
		var locallySaved = LoadLocallySavedBatches();
        BatchesList += (getUIMode() == SCAN_MODE || LocalBatchesOnly) ? locallySaved : LoadRemoteBatches();
        BatchesList += "</SELECT>";
        
        oOpenBatchDlgContent.innerHTML = BatchesList; 
        var oBatches = oOpenBatchDlgContent.document.getElementById("batches");
        oBatches.multiple = IsMultiple;
        
        
    }
    catch(e) {
        ShowErrorAlert("Error occurred", e.name, e.number, e.description, oPDIControl.GetContext(), true);
        return 0;
    }
    
    return nDocCount > 0;
}

function LoadRemoteBatches()
{
	var oIAProcesses = getControlPanel().oIAProcesses;

	var BatchesList = new Array();


	if ( oIAProcesses != null ) {
		var nCount = oIAProcesses.Count();

		for (i = 0; i < nCount; i++) {
			var strBatchName = escapeHTML(oIAProcesses.Item(i).GetFullBatchName());

			BatchesList.push( "<option value='" + escape(strBatchName) + "|" + oIAProcesses.Item(i).GetProcessID() + "|" + "'>"+strBatchName+"</option>");
		}
	}

	return BatchesList.join("");
}

function OnSelectBatchesListOptions(BatchesListOptions) {
    var batches = document.getElementById("batches");
	var BatchesList = "";
    if ( BatchesListOptions != null ) {
        switch ( Number(BatchesListOptions.options[BatchesListOptions.selectedIndex].value) ) {
            case 0:
                BatchesList += LoadLocallySavedBatches();
                break;
            case 1:
				BatchesList += LoadRemoteBatches();
                break;
            default:
                break;
        }
        
        
        if(document.all) //IE Hack. IE trancates first item
            BatchesList = "<option>truncatethis</option>" + BatchesList;
        
        batches.innerHTML = BatchesList;
        
        if(document.all)  //refresh list
        { 
            //IE workaround
            batches.outerHTML = batches.outerHTML;
        }
        
    }
}

function LoadProcesses( offline ) {
	if(oIAProcesses == null)
	    oIAProcesses = new IAProcesses( offline, getUIMode() );
    if(oIAProfiles == null)
        oIAProfiles = new IAScanProfiles(offline);
//oIAProcesses.Load( offline );
}

function showHideTooltip( obj ) {
    if ( obj.selectedIndex >= 0 ) {
        document.getElementById("tooltip").innerHTML = obj.options[obj.selectedIndex].innerText;
        if(event.type == "mouseleave") {
            document.getElementById("tooltip").style.display = "none";
        }
        else {
            document.getElementById("tooltip").style.display = "block";
        }
    }
}

function OnOpenProgressDlg( Title, ContentID ) {
    document.getElementById( ContentID ).setAttribute("closed", false);
    
    /*if ( nImagesToProcessCount )
        {
                document.getElementById("ProgressBar").style.display = "block";
                document.getElementById("ScanProgressBar").style.display = "none";
        }
        else
        {*/
    document.getElementById("ProgressBar").style.display = "none";
    document.getElementById("ScanProgressBar").style.display = "block";
    //}
    
    oPDIControl.Hide();
    window.top.showPopWin(document.getElementById(ContentID), Title, 310, 100, null, CloseCancelWindow );
    
    //if ( nImagesToProcessCount )	
    showProgress( 0 );
}

function IsCancelClosed() {
    var bClosed = document.getElementById( "ProgressDlg" ).getAttribute("closed");
    return ( bClosed == true || bClosed == "true" );
}

function CloseCancelWindow() {
    if ( !getControlPanel().IsCancelClosed() ) {
        getControlPanel().oPDIControl.GetScanner().FinishCurrentTask();

		if(SAVE_AFTER_SCANNING) {
			getControlPanel().OnSaveBatch(false);
		}
		
        getControlPanelEl( "ProgressDlg" ).setAttribute("closed", true);
        window.top.hidePopWin(false);
        getControlPanel().oPDIControl.Show();
    }
}

function ApplyViewSettings() {
    //TODO: Check IsChanged flag for optimization
    if ( document.getElementById("chkApplySettingToAll").checked ) {
        if ( document.getElementById("radioFitToWindow").checked ) {
            OnFitToWindow();
        }	
        else {
            if ( document.getElementById("radioActualSize").checked )
                OnActualSize();
        }		
        OnSetOrientation();
        OnInvert( document.getElementById("chkInvert").checked );
        
        OnBrightness( document.getElementById("brightness_display").getAttribute("value") );
        OnContrast( document.getElementById("contrast_display").getAttribute("value") );
    }
    else {
        SetDefaultViewSettings();
    }
    
    if ( getControlPanel().oPDIControl.IsMirrored() )
        getControlPanel().oPDIControl.Mirror();
    
    IsPageModified = false;
    IsAnnotationsModified = false;
}

function GetNumberFromTag(val, defVal)
{
	if(val == null || val == "")
		return defVal;
	else
		return Number(val);
}

function SetDefaultViewSettings() {
    var nViewerSize = -1;
    var nFit = 0;
    var nOrientation = 0;
    var nInvert = 0;
    var nBrightness = defBrightness;
    var nContrast = defContrast;

    //load current orientation
    var oPDIControl = getControlPanel().oPDIControl;
    if ( oPDIControl.GetCurrentPageIndex() != -1  ) {
        var CurPage = oPDIControl.GetCurrentPage();
        if ( CurPage != null ) {
            var PagePDITags = CurPage.GetTags();
            if ( PagePDITags != null ) {
				nViewerSize = GetNumberFromTag(PagePDITags.GetTmpTag( "lastViewerSize" ), nViewerSize);
				nFit = GetNumberFromTag(PagePDITags.GetTmpTag( "lastFit" ), nFit);
				nOrientation = GetNumberFromTag(PagePDITags.GetTmpTag( "lastOrientation" ), nOrientation);
				nInvert = GetNumberFromTag(PagePDITags.GetTmpTag( "lastInvert" ), nInvert);
				nBrightness = GetNumberFromTag(PagePDITags.GetTmpTag( "lastBrightness" ), nBrightness);
				nContrast = GetNumberFromTag(PagePDITags.GetTmpTag( "lastContrast" ), nContrast);
            }
        }
    }

    if ( nViewerSize >= document.getElementById("selViewerSize").options.length || nViewerSize < 0 )
        nViewerSize = document.getElementById("selViewerSize").options.length - 1;
    document.getElementById("selViewerSize").selectedIndex = nViewerSize;
    SetViewerSize();


    if ( nOrientation >= document.getElementById("selOrientation").options.length || nOrientation < 0 )
        nOrientation = 0;
    document.getElementById("selOrientation").selectedIndex = nOrientation;
    OnSetOrientation();
    
    if ( nFit > 3 || nFit < 0 )
        nFit = 0;
    document.getElementById("radioFitToWindow").checked = nFit == 0;
    document.getElementById("radioActualSize").checked = nFit == 1;
    document.getElementById("radioCustomSize").checked = nFit == 2;
	oPDIControl.FitPage();

    document.getElementById("chkInvert").checked = nInvert != 0;
    OnInvert( document.getElementById("chkInvert").checked );

    getControlPanel().SetSliderValue(
        "brightness_slider", "brightness_display", nBrightness);
    getControlPanel().SetSliderValue(
        "contrast_slider", "contrast_display", nContrast);

    IsPageModified = false;
    IsAnnotationsModified = false;
    //OnBrightness(nBrightness);
    //OnContrast(nContrast);
    
}

function SaveCurrentViewSettings()
{
	if(document.getElementById("chkApplySettingToAll").checked)
		return;
	
        //save current orientation
        if ( oPDIControl.GetCurrentPageIndex() != -1  ) {
            var CurPage = oPDIControl.GetCurrentPage();
            if ( CurPage != null ) {
                var PagePDITags = CurPage.GetTags();
                if ( PagePDITags != null ) {
                    PagePDITags.SetTmpTag( "lastViewerSize", document.getElementById("selViewerSize").selectedIndex );

					var lastFit = 0;
					if(document.getElementById("radioActualSize").checked)
						lastFit = 1;
					else if(document.getElementById("radioCustomSize").checked)
						lastFit = 2;
                    PagePDITags.SetTmpTag( "lastFit", lastFit );
                    PagePDITags.SetTmpTag( "lastOrientation", document.getElementById("selOrientation").selectedIndex );
                    PagePDITags.SetTmpTag( "lastInvert", document.getElementById("chkInvert").checked ? 1 : 0 );
                    PagePDITags.SetTmpTag( "lastBrightness", document.getElementById("brightness_display").getAttribute("value") );
                    PagePDITags.SetTmpTag( "lastContrast", document.getElementById("contrast_display").getAttribute("value") );
                }
            }
        }

}

function OnScanImage() {
    ApplyViewSettings();
    var nCount = getControlPanel().oPDIControl.GetScanner().GetScannedImageCount();
    
    if ( !getControlPanel().g_bPreserveImporting ) {
        if ( nCount > 0 )
            getControlPanelEl("btnAdvancedIndexing").disabled = false;
    }
    
    showProgress( nCount ); //import images
    return getControlPanel().IsCancelClosed();
}

function showProgress(currentValue ) {
    //Max width of progress bar     
    //var iProgresBarWidth = 250;
    var oPopupFrame = window.top.document.getElementById("popupFrame");
    
    //var oProgresBar = oPopupFrame.getElementsByTagName("DIV")[0]; //ProgressBar
    var oProgresBarText = oPopupFrame.getElementsByTagName("SPAN")[0]; //ProgressBarText
    //if (currentValue != maxValue)
    //{
    if (currentValue==0) {
        oProgresBarText.style.display = "block";
    //oProgresBar.style.display = "block";
    }
    //Show text 
    
    oProgresBarText.innerText = ( g_bPreserveImporting ? "Files count:" : "Images count:" ) + " " + currentValue;
//Set width of progress bar     
//oProgresBar.style.width = Math.round(currentValue/maxValue*iProgresBarWidth) + "px";
//}
}	

// Displays or hides annotations buttons
/*function OnDisplayAnnotationToolbar()
{
        //TODO: no unnamed constants!
        var bIsChecked = getControlPanelFrameEl("chkDisplayAnnotationToolbar").checked ;
        if ( bIsChecked )
        {
                //window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoButtonsTbl").style.display="inline";
                getControlPanel().oPDIControl.SetAnnotationsMode(2);
                OpenAnnotationsToolbar();
        }
        else
        {
                //window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoButtonsTbl").style.display="none";
                getControlPanel().oPDIControl.SetAnnotationsMode(1);
                CloseAnnotationsToolbar();
        }	
}*/

// Import annotation from file on the current page
function OnAnnotationsImport() {
    if ( getControlPanel().oPDIControl.AnnotationsImportFromFile() )
        IsAnnotationsModified = true;
}

// Delete annotations from the current page
function OnAnnotationsDelete() {
    getControlPanel().oPDIControl.AnnotationsDelete();
}

// Annotation Tools:
// 1 - Draw text annotation
// 2 - Draw freeHand annotation
// 3 - Draw hilite annotation
// 4 - Draw popup annotation
// 5 - Draw redact annotation
// 6 - Draw arrow annotation
// 7 - Draw box annotation
// 8 - Draw stamp annotation
// 9 - Draw ellipse annotation
// 10 - Draw polyline annotation
function OnDrawAnnotation(nTool) {	
    getControlPanel().oPDIControl.EnableStickyMode();
    getControlPanel().oPDIControl.SelectAnnotationTool(nTool);
    IsAnnotationsModified = true;
}	

function OnAnnotationsSetup(nTool) {
    getControlPanel().oPDIControl.SetupAnnotationTool(nTool);
    getControlPanel().oPDIControl.SelectAnnotationTool(nTool);
    return false;
}

/*function CloseAnnotationsToolbar()
{
        if ( annoWindow != null )
        {
                annoWindow.close();
                //getControlPanelFrameEl("chkDisplayAnnotationToolbar").checked = false;
                annoWindow = null;
                getControlPanel().oPDIControl.DisableStickyMode();
                getControlPanel().oPDIControl.SetAnnotationsMode(1);
        }	
}
 
function OpenAnnotationsToolbar()
{	
        getControlPanel().oPDIControl.EnableStickyMode();
        annoWindow = window.top.open("", 'Annotations',"center=true,height=320,width=20,status=off,toolbar=off,titlebar=no,resizable=no,scrollbars=no,menubar=no");
 
        var strHtmlText;
        strHtmlText = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>";
        strHtmlText += "<html>";
        strHtmlText += "<head>\n";
        strHtmlText += "<script type='text/javascript'>window.opener.frames['ControlPanelFrame'].OnAnnotationEdit();</script>\n";
        strHtmlText += "<LINK href='../content/styles.css' type='text/css' rel='stylesheet'/>\n";
        strHtmlText += "</head>";
        strHtmlText += "<body class='two' onbeforeunload='if ( window.opener != null && window.opener.frames[\"ControlPanelFrame\"] != null ) window.opener.frames[\"ControlPanelFrame\"].CloseAnnotationsToolbar(); else window.close();'>\n";
        strHtmlText += "<DIV id='ImageViewPanel' style='PADDING:0px;MARGIN:0px;WIDTH:100%;POSITION:absolute;HEIGHT:100%;'>";
        strHtmlText += window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoButtonsTbl").outerHTML;
        strHtmlText += "</div>";
        strHtmlText += "<script type='text/javascript'>document.getElementById('AnnoButtonsTbl').style.display = 'block';</script>";
        strHtmlText += "</body>";
        strHtmlText += "</html>";
 
        annoWindow.document.write(strHtmlText);	
}	
 */

function OnAnnotationsHide() {
    getControlPanel().oPDIControl.SetAnnotationsMode(0);
    var ShowHideBtn = window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoShowHideBtn");
    ShowHideBtn.title = "Edit annotations";
    ShowHideBtn.onclick=getControlPanel().OnAnnotationEdit;
    
}

function OnAnnotationsShow() {
    getControlPanel().oPDIControl.SetAnnotationsMode(1);
    var ShowHideBtn = window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoShowHideBtn");
    ShowHideBtn.title = "Hide annotations";
    ShowHideBtn.onclick=getControlPanel().OnAnnotationsHide;
}

function OnAnnotationEdit() {
    //TODO: no unnamed constants!
    getControlPanel().oPDIControl.SetAnnotationsMode(2);
//var ShowHideBtn = window.top.frames["ImageViewPanelFrame"].document.getElementById("AnnoShowHideBtn");
//ShowHideBtn.title = "Hide annotations";
//ShowHideBtn.onclick=getControlPanel().OnAnnotationsHide;
}


//Indexing 

function OnCancelBatch() {
    HideBatchActions();
    ClearAll();
}

function OnChangeCurrentPage( imgIndex, Level, bAlwaysValidate ) {
    try {
        if ( IsPageModified || IsAnnotationsModified ) {
            OnSaveModifications();
            IsPageModified = false;
            IsAnnotationsModified = false;
        }
        
        if ( getUIMode() == INDEX_MODE && oIABatch.GetIndexSettings().IsEnabledAutomaticZoom() && oPDIControl != null )
            oPDIControl.HideCurrentZone();

		if(Level == 0)
			oPDIControl.SelectPageByIndex(imgIndex);

		SaveCurrentViewSettings();

        if ( getUIMode() == SCAN_MODE || getUIMode() == RESCAN_MODE) {
            if ( oPDIControl != null && scan_index_validator != null ) {
                var PDITags;
                if ( oPDIControl != null ) {
                    var CurDocument = oPDIControl.GetDocument();
                    if ( CurDocument != null ) {
                        if ( Level != 0 )
                            PDITags = CurDocument.GetTags();
                        else {
                            if ( oPDIControl.GetCurrentPageIndex() != -1  )
                                PDITags = oPDIControl.GetCurrentPage().GetTags();
                            else
                                PDITags = null;
                        }
                    }
                }

				var index_fields = scan_index_validator.GetIndexFieldsXML();
                if ( PDITags && index_fields != "" ) {
                    if ( Level != 0 )
                        PDITags.SetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + imgIndex), index_fields );
                    else
					{
                        PDITags.SetTag(AddStatusPrefix(EINPUT_PAGE_INDEX_FIELDS), index_fields );
						if(scan_index_validator.GetWasModified())
							PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), PAGE_STATUS_INDEXED );
					}

                }
            }
        }
        else if ( getUIMode() == INDEX_MODE ) {
            if ( imgIndex != null && oPDIControl.GetCurrentPageIndex() != -1  && Level == 0 ) {
                window.top.frames["IndexFieldsPanelFrame"].document.body.style.display = "block";
                
                var CurPage = oPDIControl.GetCurrentPage();
                var PDITags = null;
                if ( CurPage != null )
                    PDITags = CurPage.GetTags();
                
                var Status = Number( PDITags.GetTag( AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS) ) );
                if ( Status == null || Status == "" ) {
                    PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), PAGE_STATUS_NOT_INDEXED );
                    Status = PAGE_STATUS_NOT_INDEXED;
                }
                
                var PageValidationType = oIABatch.GetIndexSettings().GetPageValidationType();
                switch ( Status ) {
                    case PAGE_STATUS_VALIDATION_FAILED:
                    case PAGE_STATUS_MODIFIED:
                        if ( bAlwaysValidate != null && bAlwaysValidate == true ) {
                            ValidatePage( oPDIControl, eValidationType.AlwaysValidate );
                        }
                        else {
                            ValidatePage( oPDIControl, PageValidationType );
                        }
                        break;
                        
                    //no validation if no changes
                    case PAGE_STATUS_INDEXED:
                    case PAGE_STATUS_NOT_INDEXED:
                        
                        if ( bAlwaysValidate != null && bAlwaysValidate == true ) {
                            //ValidatePage( oPDIControl, PageValidationType );
                            ValidatePage( oPDIControl, eValidationType.AlwaysValidate );
                        }
                        else {
                            //if ( PageValidationType == eValidationType.AlwaysValidate )
                            ValidatePage( oPDIControl, PageValidationType );
                        }
                        break;
                    default:
                        ValidatePage( oPDIControl, PageValidationType );
                        break;
                }
            }
            else {
                window.top.frames["IndexFieldsPanelFrame"].document.body.style.display = "none";
            }
        //If no exceptions
        //Hide ocr data
        //if ( oIABatch != null && oPDIControl != null && oIABatch.GetIndexSettings().IsAllowRubberbandZone() )
        //{
        //    oPDIControl.HideOCRZones();
        //}
        }
    }
    catch(e) {
        if ( typeof(e) != 'string' )
            ShowErrorAlert("Error in OnChangeCurrentPage", e.name, e.number, e.description, null, true);
        
        return false;
    }
    return true;
}

function OnIndexFieldsModified() {
    if ( getUIMode() == INDEX_MODE ) {
        //change page status to modified
        var CurPage = oPDIControl.GetCurrentPage();
        var PDITags = null;
        if ( CurPage != null ) {
            PDITags = CurPage.GetTags();
            if ( PDITags != null ) {
                PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), PAGE_STATUS_MODIFIED );
            }
        }
        
        getTreeViewPanel().oIATree.DrawNotIndexedBorder();
        
        getControlPanelEl("btnAcceptTask").disabled = false;
    }
}

function MarkAllNodesAsFailed(oPDIControl, oValidator, PDITags, value)
{
    if ( PDITags )
        PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), value);

    getTreeViewPanel().oIATree.DrawNotIndexedBorder();

    var LastFailedControlID = oValidator.GetLastFailedControlID();
    if ( LastFailedControlID != null ) {
        LastFailedControl = window.top.frames["IndexFieldsPanelFrame"].document.getElementById( LastFailedControlID );
        if ( LastFailedControl != null && !LastFailedControl.disabled ) {
            var prevIndex = oPDIControl.GetCurrentPageIndex();
            var level = Number(LastFailedControl.getAttribute("Level"));
            getTreeViewPanel().oIATree.DrawNotIndexedBorderForLevel(level, function(el){
                var imgIndex = Number(el.getAttribute("imgIndex"));
                oPDIControl.SelectPageByIndex(imgIndex);
                var curPage = oPDIControl.GetCurrentPage();
                curPage.GetTags().SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), value);
            });
            oPDIControl.SelectPageByIndex(prevIndex);
        }
    }
}

function ValidatePage( oPDIControl, PageValidationType ) {
    var oValidator = window.top.frames["IndexFieldsPanelFrame"].g_objValidator;
    
    var CurPage = oPDIControl.GetCurrentPage();
    var PDITags = null;
    if ( CurPage != null )
        PDITags = CurPage.GetTags();
    
    if ( oValidator != null ) {
        if ( PageValidationType != eValidationType.NeverValidate ) {
            var bValidated = oValidator.Validate();
            if ( !bValidated ) {
                if( oValidator.IsOfflineValidation() || !confirm("Do you wish to save current changes and leave page as not indexed?") ) {

                    MarkAllNodesAsFailed(oPDIControl, oValidator, PDITags, PAGE_STATUS_VALIDATION_FAILED);
                    
                    var LastFailedControlID = oValidator.GetLastFailedControlID();
                    if ( LastFailedControlID != null ) {
                        LastFailedControl = window.top.frames["IndexFieldsPanelFrame"].document.getElementById( LastFailedControlID );
                        if ( LastFailedControl != null && !LastFailedControl.disabled ) {
                            LastFailedControl.setAttribute("InternalFocus", true);
                            LastFailedControl.focus();
                        }
                    }
                    
                    if ( !oValidator.IsOfflineValidation() )
                        throw "Validation failed!";
                }
                else {
                    MarkAllNodesAsFailed(oPDIControl, oValidator, PDITags, PAGE_STATUS_NOT_INDEXED);
                }
            }
        }
        
        if ( oPDIControl != null ) {
            if ( PDITags ) {
                try {
                    var IndexFieldsLevels = new Array();
                    for ( var i = 0; i < 8; i++ ) {
                        IndexFieldsLevels[i] = "<index_fields>";
                    }
                    
                    var doc = GetXMLParser( oValidator.GetIndexFieldsXML() );
                    var root = doc.documentElement;  
                    
                    if ( root != null ) {
                        var nCount = root.childNodes.length;
                        for ( var i = 0; i < nCount; i++ ) {
                            var index_item = root.childNodes(i);
                            var numLevel = Number(index_item.getElementsByTagName("level")[0].text);
                            IndexFieldsLevels[numLevel] += index_item.xml;
                        }
                    }
                    
                    for ( var i = 0; i < 8; i++ ) {
                        IndexFieldsLevels[i] += "</index_fields>";
                    }    
                    
                    var rootParent = null;
                    var pageNode = getTreeViewPanel().oIATree.GetCurrentlyActiveItem(); //should be page
                    if ( pageNode.parentElement != null ) {
                        var CurDocument = oPDIControl.GetDocument();
                        var docTags = CurDocument.GetTags();
                        var curNode = pageNode.parentElement.parentElement;
                        while ( curNode != null && curNode.tagName == "LI" ) {
                            var NodeLevel = Number(curNode.getAttribute("Level"));
                            
                            docTags.SetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + curNode.id), IndexFieldsLevels[NodeLevel] );
                            
                            if ( oValidator.minLevel > 0 && NodeLevel == oValidator.minLevel ) {
                                rootParent = curNode;
                            }
                            
                            curNode = curNode.parentElement.parentElement;
                        }
                    }
                    
                    PDITags.SetTag(AddStatusPrefix(EINPUT_PAGE_INDEX_FIELDS), IndexFieldsLevels[0] );
                    if ( !oValidator.IsOfflineValidation() && bValidated ) {
                        PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), PAGE_STATUS_INDEXED );
                        getTreeViewPanel().oIATree.ClearBorderForCurrent();
                        
                        if ( oValidator.minLevel > 0 )
                            MarkNodesAsIndexed( rootParent );
                    }
                }
                catch (e) {
                    ShowErrorAlert("Error occurred", e.name, e.number, e.description, oPDIControl.GetContext(), true);
                }
            }
        }
    }
    else {
        //no indexing fields 
        if ( PDITags != null ) {
            PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), PAGE_STATUS_INDEXED );
            getTreeViewPanel().oIATree.ClearBorderForCurrent();
        }
    }
    return true;
}

function MarkNodesAsIndexed( Parent ) {
    if ( Parent != null ) {
        var subItems = Parent.getElementsByTagName("LI");
        var nCount = subItems.length;
        var oPDIDocument = oPDIControl.GetDocument();
        
        for ( var i = 0; i < nCount; i++ ) {
            var item = subItems[i];
            
            if ( Number(item.getAttribute("Level")) == 0 ) //if page
            {
                var index = Number(item.getAttribute("imgIndex"));
                
                var CurPage = oPDIDocument.GetPageByIndex(index);
                var PDITags = null;
                if ( CurPage != null )
                    PDITags = CurPage.GetTags();
                
                if ( PDITags != null )
                    PDITags.SetTag(AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS), PAGE_STATUS_INDEXED );
                
                getTreeViewPanel().oIATree.ClearItemBorder( item );
            }
        }
    }
/*    if ( curNode != null )//update siblings
    {
 
    }
    else
    {
        var pageNode = getTreeViewPanel().oIATree.GetCurrentlyActiveItem(); //should be page
        if ( pageNode.parentElement != null )
        {
            var curNode = pageNode.parentElement.parentElement;
            while ( curNode != null && curNode.tagName == "LI" )
            {
                var NodeLevel = Number(curNode.getAttribute("Level"));
                if ( NodeLevel == Level )
                    break;
                var CurDocument = oPDIControl.GetDocument();
                var docTags = CurDocument.GetTags();
 
                oValidator.SetIndexFields( docTags.GetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + curNode.id)) );
                curNode = curNode.parentElement.parentElement;
            }
        }
    }*/
}

function FillRescanReason(imgIndex, Level)
{
	if(getUIMode() != RESCAN_MODE)
		return;

	if(Level != 0)
	{
		ClearRescanReasonTab();
		return;
	}

	var PDITags;
	if ( oPDIControl != null ) 
		PDITags = oPDIControl.GetCurrentPage().GetTags();

	if ( PDITags ) {
		var tag = PDITags.GetTag(AddStatusPrefix(EINPUT_RESCAN_REASON )) ;
		//tag = "This is text of rescan reason";
		if(tag == null)
			ClearRescanReasonTab();
		else
			document.getElementById("sc4").innerHTML = 
			'<table ><tr ><td><fieldset style="height:150px"><legend>Rescan Reason</legend><table><tr style="height:10px"><td></td></tr><tr><td class="layoutTD">'+
			escapeHTMLMultistring(tag, "<br>")+
			'</td></tr></table></fieldset></td></tr><tr ></tr></table>';

	}
}

function GetPrefixForIndex(Level)
{
	var prefix = "";

	switch ( Level ) {
		case 0:
			prefix = "Page fields:<BR>";
			break;
		case 1:
			prefix = "Document fields:<BR>";
			break;
		case 2:
			prefix = "Folder fields:<BR>";
			break;
		case 3:
			prefix = "Stack fields:<BR>";
			break;
		case 4:
			prefix = "Level 4 fields:<BR>";
			break;
		case 5:
			prefix = "Level 5 fields:<BR>";
			break;
		case 6:
			prefix = "Level 6 fields:<BR>";
			break;
		default:
			prefix = "";
			break;
	}

	return prefix;
}

function FillScanIndexFields(imgIndex, Level )
{
	var IndexHTML = oIABatch.GetIndexFieldsHTMLByLevel(Number(Level));
	if ( scan_index_validator != null ) {
		scan_index_validator.ClearIndexFields();

		if ( IndexHTML != null && IndexHTML != "" ) {

			document.getElementById("sc3").innerHTML = GetPrefixForIndex(Level) + IndexHTML; //load index fields
			scan_index_validator.Initialize( document.getElementById("sc3") );

			var PDITags;
			if ( oPDIControl != null ) {
				var CurDocument = oPDIControl.GetDocument();
				if ( CurDocument != null ) {
					if ( Level != 0 )
						PDITags = CurDocument.GetTags();
					else
						PDITags = oPDIControl.GetCurrentPage().GetTags();
				}
			}

			if ( PDITags ) {
				if ( Level != 0 )
					scan_index_validator.SetIndexFields( PDITags.GetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + imgIndex )) );
				else
					scan_index_validator.SetIndexFields( PDITags.GetTag(AddStatusPrefix(EINPUT_PAGE_INDEX_FIELDS)) );
			}
		}
		else {
			ClearIndexFieldsTab();
		}
	}
}

function FillIndexTab( imgIndex, Level )
{
	if ( g_CurrentFieldID != null ) {
		var field = window.top.frames["IndexFieldsPanelFrame"].document.getElementById(g_CurrentFieldID);
		if ( field != null ) {
			field.style.backgroundColor = "";
			field.setAttribute("current_field", false);
		}
		g_CurrentFieldID = null;
	}
	if ( getUIMode() == INDEX_MODE ) {
		if ( imgIndex != null && Level == 0) {
			
			oPDIControl.SelectPageByIndex(imgIndex);

			window.top.frames["IndexFieldsPanelFrame"].document.body.style.display = "block";
			if ( oPDIControl != null ) {
				var CurPage = oPDIControl.GetCurrentPage();
				if ( CurPage != null ) {
					var PDITags = CurPage.GetTags();
					if ( PDITags ) {
						//Draw ocr data
						if ( oIABatch.GetIndexSettings().IsAllowRubberbandZone() ) {
							oPDIControl.ShowOCRZones( PDITags.GetTag(AddStatusPrefix(EINPUT_PAGE_OCR_ZONES)) )
						}

						var oValidator = window.top.frames["IndexFieldsPanelFrame"].g_objValidator;

						if ( oValidator != null ) {
							oValidator.ClearIndexFields();
							oValidator.ResetMaskEditControls();
							window.top.frames["IndexFieldsPanelFrame"].document.body.focus();
							oValidator.SetIndexFields( PDITags.GetTag(AddStatusPrefix(EINPUT_PAGE_INDEX_FIELDS)) );

							var pageNode = getTreeViewPanel().oIATree.GetCurrentlyActiveItem(); //should be page
							if ( pageNode.parentElement != null ) {
								var curNode = pageNode.parentElement.parentElement;
								while ( curNode != null && curNode.tagName == "LI" ) {
									var NodeLevel = Number(curNode.getAttribute("Level"));

									var CurDocument = oPDIControl.GetDocument();
									var docTags = CurDocument.GetTags();

									oValidator.SetIndexFields( docTags.GetTag(AddStatusPrefix(EINPUT_TREE_LEVEL_INDEX_FIELDS + curNode.id)) );
									curNode = curNode.parentElement.parentElement;
								}
							}
							//on fields loaded event
							oValidator.PrepopulateFields();
						}
					}
				}
			}
		}
		else {
			window.top.frames["IndexFieldsPanelFrame"].document.body.style.display = "none";
		}
	}
}

function OnHighlightItem( imgIndex, Level ) {
	if(Level == 0)
		oPDIControl.SelectPageByIndex(imgIndex);

	if ( getUIMode() == SCAN_MODE || getUIMode() == RESCAN_MODE)
	{
		FillScanIndexFields( imgIndex, Level );
		FillRescanReason( imgIndex, Level );
	}
	else
		FillIndexTab( imgIndex, Level);
	
	if ( getUIMode() == SCAN_MODE )
		getControlPanelEl("btnAdvancedIndexing").disabled = getTreeViewPanel().oIATree.GetPageCount() <= 0;
    
    ApplyViewSettings();
}

function OnDisableLoginControls( bOfflineChecked ) {
    if ( bOfflineChecked )
        document.getElementById("SERVER").style.backgroundColor = "#CCC";
    else
        document.getElementById("SERVER").style.backgroundColor = "";
    
    document.getElementById("SERVER").disabled   = bOfflineChecked;
    
    if ( bOfflineChecked )
        document.getElementById("USERNAME").style.backgroundColor = "#CCC";
    else
        document.getElementById("USERNAME").style.backgroundColor = "";
    
    document.getElementById("USERNAME").disabled = bOfflineChecked;
    
    if ( bOfflineChecked )
        document.getElementById("PASSWORD").style.backgroundColor = "#CCC";
    else
        document.getElementById("PASSWORD").style.backgroundColor = "";
    
    document.getElementById("PASSWORD").disabled = bOfflineChecked;
    
    if ( bOfflineChecked )
        document.getElementById("DOMAIN").style.backgroundColor = "#CCC";
    else
        document.getElementById("DOMAIN").style.backgroundColor = "";
    
    document.getElementById("DOMAIN").disabled   = bOfflineChecked;
    
    if ( bOfflineChecked )
        document.getElementById("DEPARTMENT").style.backgroundColor = "#CCC";
    else
        document.getElementById("DEPARTMENT").style.backgroundColor = "";
    
    document.getElementById("DEPARTMENT").disabled   = bOfflineChecked;
}

/*function OnAcceptButton( e, btnAccept, btnCancel )
{
    if ( e && btnAccept && btnCancel )
    {
        switch ( e.keyCode )
        {
            case 13: //Enter
            {  
                btnAccept.click();  
                return true;  
            } 
            case 27: //Esc
            {  
                btnCancel.click();  
                return true;  
            } 
        } 
    }    
}*/

//Indexing panel

var runSingleBatcthFlag = false;
function OnRunSingleBatch( Title, ContentID ) {
   	if(window.top.isDownloading)
	{
		alert("Could not perform the operation while a batch is being sent");
		return;
	}

    g_bIsIndexed = false;
    if ( LoadBatches( false, !navigator.onLine ) )
	{
        OpenDialog( Title, ContentID, 400, 400, RunSingleBatchRetFunc );
	}
	else
		if(nDocCount == 0)
			alert("No batches to select");

}

function OnVinSingleBatch( Title, ContentID )
{
	if (vinSelectedBatch == false || vinSelectedBatch == null)
	{
	//alert("Please, first Run Single Batch."); //vince 10 April 2008
	}
	else {
		g_bIsIndexed = false;
		if ( !LoadBatches( false, !navigator.onLine, true ) )
		{
			if(nDocCount < 0)
				alert("Could not receive next task. Try to reconnect to InputAccel");
		}
		else
		{
			//vinSelectedBatch = "april10_10_20080410-151317_127000000001|3741|";
			if ( vinSelectedBatch.split("|")[2] == "" )
			{
				var tasks = oIAProcesses.GetByID( vinSelectedBatch.split("|")[1] );
				if (tasks == null || tasks == "undefined" || tasks.length < 1)
				{
				//	alert("The batch has already been completed."); //vince 10 April 2008
				}
				else {
					
					oIABatch = oIAProcesses.GetByID( vinSelectedBatch.split("|")[1] );
					//getControlPanel().oIABatch = getControlPanel().oIAProcesses.GetByID( vinSelectedBatch.split("|")[1] );
					if(!confirm("Download the " + oIABatch.FullBatchName + " batch which is ready for processing?"))
						return;

					//alert(vinSelectedBatch); //vince 10 April 2008
					OpenDownloadProgress("Downloading Batch...", "ProgressDlg");
					ReportDownloadProgress("Connecting to the InputAccel Server...");

					if ( iStartDownload )
					//if ( getControlPanel().iStartDownload )
					{
						clearTimeout( iStartDownload );
						//getControlPanel().clearTimeout( getControlPanel().iStartDownload );
						iStartDownload = null;
					//getControlPanel().iStartDownload = null;
					}
					iStartDownload = setTimeout("AsyncStartDownload()", 10);
				//getControlPanel().iStartDownload = getControlPanel().setTimeout("getControlPanel().AsyncStartDownload()", 10);
				//alert(vinSelectedBatch); //vince 10 April 2008
				}
			}
			else
			{
				alert('You should check OnVinSingleBatch if you see this message')
				OpenBatchRetFunc( SelectedBatch );
			}
		}
	}
}

function RunSingleBatchRetFunc( SelectedBatch )
{
    if ( SelectedBatch[0].split("|")[2] == "" )
    {
        //alert(SelectedBatch); //vince 10 April 2008
        vinSelectedBatch=SelectedBatch[0]; //vince 10 April 2008
        //alert(vinSelectedBatch); //vince 10 April 2008
        getControlPanel().oIABatch = getControlPanel().oIAProcesses.GetByID( SelectedBatch[0].split("|")[1] );

        OpenDownloadProgress("Downloading Batch...", "ProgressDlg");
        ReportDownloadProgress("Connecting InputAccel server...");

        if ( getControlPanel().iStartDownload )
        {
            getControlPanel().clearTimeout( getControlPanel().iStartDownload );
            getControlPanel().iStartDownload = null;
        }
        getControlPanel().iStartDownload = getControlPanel().setTimeout("getControlPanel().AsyncStartDownload()", 10);
    }
    else
    {
        //alert("hello3"); //vince 10 April 2008
        OpenBatchRetFunc( SelectedBatch );
    }
}

function RunSingleBatchRetFuncDepricated( SelectedBatch ) {
	runSingleBatcthFlag = true;

    if ( SelectedBatch[0].split("|")[2] == "" ) {
        getControlPanel().oIABatch = getControlPanel().oIAProcesses.GetByID( SelectedBatch[0].split("|")[1] );
        
        OpenDownloadProgress("Downloading Batch...", "ProgressDlg");
        ReportDownloadProgress("Connecting InputAccel server...");
        
        if ( getControlPanel().iStartDownload ) {
            getControlPanel().clearTimeout( getControlPanel().iStartDownload );
            getControlPanel().iStartDownload = null;
        }
        getControlPanel().iStartDownload = getControlPanel().setTimeout("getControlPanel().AsyncStartDownload()", 10);
    }
    else {
        OpenBatchRetFunc( SelectedBatch );
    }
}

function DownloadBatch() {
	if(!CheckConnectionWithLogin(true))
		return;
	
    if ( g_CurrDocID != null ) {
        ReportDownloadProgress("Downloading batch...");

		var setup_values_prefix = INDEX_SETUP_VALUES;
		var get_value_prefix = INDEX_GET_VALUE_PREFIX;
		if(getUIMode() == RESCAN_MODE ) {
			setup_values_prefix = RESCAN_SETUP_VALUES;
			get_value_prefix = RESCAN_GET_VALUE_PREFIX;
		}

        //Set current batch
        var arrSetupValues = setup_values_prefix.split("|");
        var nValuesCount = arrSetupValues.length;
        
        if ( getControlPanel().oIABatch.GetInstance() == "" ) {
            var CurProcess = getControlPanel().oIAProcesses.GetByProcessName( getControlPanel().oIABatch.GetProcessName() );
            getControlPanel().oIABatch.SetInstance( CurProcess.GetInstance() );
        }
        
        for ( var k = 0; k < nValuesCount; k++ ) {
            getControlPanel().oIAProcesses.LoadSetupValues( getControlPanel().oIABatch,
                false,
                null,
                get_value_prefix,
                getControlPanel().oIABatch.GetInstance(),
                arrSetupValues[k]);
        }
        
        try {
            window.top.sendDoc = null;
            var DownloadWindow = window.top.open(
				FRAME_SEND_URL_PREFIX+getUIMode(),
				'_blank',"center=true,height=200,width=400,status=off,toolbar=off,titlebar=no,resizable=no,scrollbars=no,menubar=no");
            if ( DownloadWindow != null ) {
				// Crutches, crutches, crutches....
			   //window.top.DocID = g_CurrDocID;
                window.top.tmpDocID = g_CurrDocID;
                
                if ( getControlPanel().oIABatch.GetIndexSettings().GetImageStreamingType() != 0 ) {
                    window.top.tmpContentPart = 0;
                    DownloadWindow.ContentPart = 0;
                }
                else {
                    window.top.tmpContentPart = 2;
                    DownloadWindow.ContentPart = 2;
                }
				window.top.sendDoc = false;

                if ( window.focus )
                    DownloadWindow.focus();
            }
            else {
                //cancel task
                CancelIndexTask( g_CurrDocID );
                alert("The Send or Receive window was blocked. Allow pop-ups from this website to send or receive documents.");
                CloseDownloadProgress();
            }
        }
        catch(e) {
            
        }
    }
}

function AsyncStartDownload() {


    var oPDIControl = getControlPanel().oPDIControl;
    oPDIControl.Log("[BEGIN] Start download");

	var prefix = INDEX_NEWPDIDOC_PREFIX;
	if(getUIMode() == RESCAN_MODE )
		prefix = RESCAN_NEWPDIDOC_PREFIX;

	var url = prefix + getControlPanel().oIABatch.GetProcessID();

    http_newpdidoc.sendGetRequest(url , NewPDIDocCallback);
	ProcessLogout(http_newpdidoc);
    
    if ( g_CurrDocID != null ) {
        oPDIControl.Log("[BEGIN] download PDI document by GUID: " + g_CurrDocID );
        DownloadBatch();
        oPDIControl.Log("[END]");
        return;
    }
    oPDIControl.Log("[WARNING] Unable to start download DocID is not specified!");
    
    CloseDownloadProgress();
    getControlPanel().ContinueRunAllBatches( true );
    
    oPDIControl.Log("[END]");
//else
//{
//    alert("Unable to start downloading the batch from the InputAccel Server!");
//}
}

function NewPDIDocCallback()
{
    g_CurrDocID = null;

    if (!http_newpdidoc.processHttpResponseXml("Unable to start download!"))
		return;

	var root = http_newpdidoc.root;

	try
	{
		/*this is a FIX for ESCAN-1326*/
		var sInstance=root.getElementsByTagName( INSTANCE )[0].childNodes(0).xml;
		g_CurrDocID =root.getElementsByTagName( NEW_DOC_GUID )[0].childNodes(0).xml;
		/*end of FIX for ESCAN-1326*/
		//g_CurrDocID = root.getElementsByTagName( RESPONSE_RESULT )[0].childNodes(0).xml;
                
		//if ( getControlPanel().oIABatch.GetInstance() == "" ) {
		var CurProcess = getControlPanel().oIAProcesses.GetByProcessName( getControlPanel().oIABatch.GetProcessName() );
		CurProcess.SetInstance(sInstance);
		getControlPanel().oIABatch.SetInstance( sInstance );
	//}
	}
	catch(e)
	{
		ASSERT(false, "It should be interesting");
		g_CurrDocID = null;
	}
                
}

function ReportDownloadProgress( msg ) {

    var oPopupFrame = window.top.document.getElementById("popupFrame");
    var oProgresBarText = oPopupFrame.getElementsByTagName("SPAN")[0]; //ProgressBarText
    oProgresBarText.innerText = msg;
}

function OpenDownloadProgress(Title, ContentID) {

    oPDIControl.Hide();
    window.top.showPopWin(document.getElementById(ContentID), Title, 310, 100, null, null, false );
    
    var oPopupFrame = window.top.document.getElementById("popupFrame");
    var oProgresBar = oPopupFrame.getElementsByTagName("DIV")[0]; //ProgressBar
    oProgresBar.style.display = "none";
    
    var oProgresBarText = oPopupFrame.getElementsByTagName("SPAN")[0]; //ProgressBarText
    oProgresBarText.style.display = "block";
    oProgresBarText.innerText = "Start downloading the batch from the InputAccel Server";
    
    oPopupFrame.getElementsByTagName("IMG")[0].style.display = "block";
    oPopupFrame.getElementsByTagName("BUTTON")[0].style.display = "none";
}

function CloseDownloadProgress() {

    getControlPanel().ShowAutoCursor();
    getControlPanel().g_CurrDocID = null;
    window.top.hidePopWin(false);
    getControlPanel().oPDIControl.Show();
}

function ContinueRunAllBatches( bConfirm ) {
    if ( (g_IsRunAllBatches == true) && (getUIMode() == INDEX_MODE || getUIMode() == RESCAN_MODE) ) {
        if ( !bConfirm || confirm("Do you want to continue run all batches?") ) {
            if ( getControlPanel().iRunAllBatches ) {
                getControlPanel().clearTimeout( getControlPanel().iRunAllBatches );
                getControlPanel().iRunAllBatches = null;
            }
            getControlPanel().iRunAllBatches = getControlPanel().setTimeout("getControlPanel().OnRunAllBatches()", 100);
        }
    }
}

function OnRunAllBatches() {
    if(window.top.isDownloading)
	{
		alert("Could not perform the operation while a batch is being sent");
		return;
	}

    g_IsRunAllBatches = true;
    
    if ( oIAProcesses == null ) {
        g_IsRunAllBatches = false;
        return;
    }
    
    if(!oIAProcesses.UpdateFast( window.top.g_CachedMode ))
	{
		g_IsRunAllBatches = false;
		return;
	}
    oIAProcesses.Load( window.top.g_CachedMode );
    UpdateProfiles();
    
    if ( oIAProcesses.Count() > 0 ) {
        var curBatch = oIAProcesses.Item(0).GetFullBatchName() + "|" + oIAProcesses.Item(0).GetProcessID()  + "|";
        var selectedBatch = new Array();
        selectedBatch[0] = curBatch;
        
        if ( confirm("Download the " + oIAProcesses.Item(0).GetFullBatchName() + " batch which is ready for processing?") ) {
            RunSingleBatchRetFunc( selectedBatch );
        }
        else {
            g_IsRunAllBatches = false;
        }
    }
	else
    {
//        alert("No batches for indexing!");
        g_IsRunAllBatches = false;
    }
}

function ShowZone() {
    if ( getUIMode() == INDEX_MODE ) {
        try {
                if ( g_CurrentFieldID != null ) {
                    var field = window.top.frames["IndexFieldsPanelFrame"].document.getElementById(g_CurrentFieldID);
                    if ( field != null ) {
                        field.style.backgroundColor = "";
                        field.setAttribute("current_field", false);
                    }
                }
                //draw focused item
                this.style.backgroundColor = "Yellow";
                this.setAttribute("current_field", true);
                g_CurrentFieldID = this.id;
				
            if ( oIABatch.GetIndexSettings().IsEnabledAutomaticZoom() ) {
                
                if ( !oIABatch.GetIndexSettings().IsAllowRubberbandZone() ) {
                    getControlPanel().oPDIControl.HideCurrentZone();
                    
                    var ZoneNumber = this.getAttribute("ZoneNumber");
                    if ( ZoneNumber != "" && ZoneNumber != null && ZoneNumber != "<none>" ) {
                        if ( getControlPanel().oPDIControl != null ) {
                            getControlPanel().oPDIControl.ShowZone( Number(ZoneNumber) );
                        }
                    }
                }
            }
        }
        catch (e) {
            ShowErrorAlert("Error ", e.name, e.number, e.description, null, true);
        }
    }    
}

function OnSelectOCRZones( text ) {
    
}

function OnPopulateWithOCR() {
    if ( getUIMode() == INDEX_MODE ) {
        if ( oPDIControl != null ) {
            var text = oPDIControl.GetOCRData();
            
            if ( g_CurrentFieldID != null ) {
                var field = window.top.frames["IndexFieldsPanelFrame"].document.getElementById(g_CurrentFieldID);
                if ( field != null && field.type.toUpperCase().indexOf("SELECT") < 0) {
                    var results = new Array();
                    results[0] = text;
                    window.top.frames["IndexFieldsPanelFrame"].PopulateIndexField( field, results, text, true, true);
                    if ( !field.disabled )
                        field.focus();
                }
            }
        }
    }
}

function OnAcceptButton() {
    if ( window.top.gPopupIsShown ) {
        var buttons = window.top.document.getElementsByTagName("BUTTON");
        if ( buttons != null ) {
            for ( var i = 0; i < buttons.length; i++ ) {
                if ( buttons[i].id.search("Close") >= 0 ) {
                    if(buttons[i].focused == null || buttons[i].focused == false)
						buttons[i].click();
                    break;
                }
            }
        }
    }
}

function OnCancelButton() {
    if ( window.top.gPopupIsShown ) {
        var buttons = window.top.document.getElementsByTagName("BUTTON");
        if ( buttons != null ) {
            for ( var i = 0; i < buttons.length; i++ ) {
                if ( buttons[i].id.search("Cancel") >= 0 ) {
                    buttons[i].click();
                    break;
                }
            }
        }
		window.top.hidePopWin(false);
    }
}

//Verify index fields and move to the next non indexed page
function OnAcceptTask() {

//IMAGE ACCESS MOD
	setCustomFields();
//IMAGE ACCESS MOD - END

        ASSERT(false, "OnAcceptTask");

    var oCurrentlyActiveItem = getTreeViewPanel().oIATree.GetCurrentlyActiveItem();
    var bValidated = true;
    if ( oCurrentlyActiveItem != null ) {
        var nLevel = Number(oCurrentlyActiveItem.getAttribute("Level"));
        try {
            if ( nLevel == 0 )
                bValidated = OnChangeCurrentPage( Number(oCurrentlyActiveItem.getAttribute("imgIndex")), nLevel, true );
            else
                bValidated = OnChangeCurrentPage( oCurrentlyActiveItem.id, nLevel, true );
        }
        catch (e) {
            bValidated = false;
        }
    }
    
    if ( bValidated ) {
        if ( oPDIControl != null ) {
            var PDIDocument = oPDIControl.GetDocument();
            var PDITags = PDIDocument.GetTags();
            var IsIndexed = ( PDITags.GetTag(AddStatusPrefix(EINPUT_BATCH_INDEXED)) == "true" ? true : false );
            if ( !IsIndexed ) {
                var CurItemID = oCurrentlyActiveItem.id;
                var nextItem = getTreeViewPanel().oIATree.GetItemByLevel(0, true, oCurrentlyActiveItem);
                var bFound = false;
                
                while ( !bFound && nextItem != null && nextItem.id != CurItemID ) {
                    var nIndex = Number(nextItem.getAttribute("imgIndex"));
                    
                    var IsPageIndexed = 0;
                    var IsAttachment = false;
                    var Page = PDIDocument.GetPageByIndex( nIndex );
                    if ( Page != null ) {
                        var PageTags = Page.GetTags();
                        if ( PageTags != null ) {
                            IsPageIndexed = Number( PageTags.GetTag( AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS) ) );
                            IsAttachment = (PageTags.GetTag( AddStatusPrefix(EINPUT_IS_ATTACHMENT) ) == "true") ? true : false;
                        }
                    }
                    
                    if ( !IsAttachment ) {
                        if ( IsPageIndexed != PAGE_STATUS_INDEXED ) {
                            try {
                                var ActiveItemTagA = nextItem.getElementsByTagName('A')[0];	
                                getTreeViewPanel().oIATree.highlightItem( ActiveItemTagA, null, true );
                                getTreeViewPanel().document.getElementById( getTreeViewPanel().oIATree.getTreeId() ).scrollTop = ActiveItemTagA.offsetTop;
                                window.top.frames["IndexFieldsPanelFrame"].OnFirstIndexingField();
                            }
                            catch (e) {
                                ShowErrorAlert("Error ", e.name, e.number, e.description, null, true);
                            }
                            
                            bFound = true;
                            break;
                        }
                    }
                    
                    nextItem = getTreeViewPanel().oIATree.GetItemByLevel(0, true, nextItem);
                }
                
                if ( nextItem != null && nextItem.id == CurItemID ) //check current item
                {
                    var nIndex = Number(nextItem.getAttribute("imgIndex"));
                    
                    var Page = PDIDocument.GetPageByIndex( nIndex );
                    if ( Page != null ) {
                        var PageTags = Page.GetTags();
                        if ( PageTags != null ) {
                            if ( Number( PageTags.GetTag( AddStatusPrefix(EINPUT_INDEX_PAGE_STATUS) ) ) != PAGE_STATUS_INDEXED )
                                bFound = true;
                        }
                    }
                }
                
                if ( !bFound ) {
                    //all fields were indexed successfully 
                    getControlPanelEl("btnAcceptTask").disabled = true;
                    PDITags.SetTag(AddStatusPrefix(EINPUT_BATCH_INDEXED), "true");
                }
            }
            else {
                getControlPanelEl("btnAcceptTask").disabled = true;
            }
        }
    }
    
    if ( getControlPanelEl("btnAcceptTask").disabled ) {
        getControlPanelEl("btnAcceptTask").className = "buttons";
    }
}

function OnAddAttachment(pageNode) {
    OnImportPages('Import Files','ProgressDlg', true, ALL_FILES, true);
}

function OnViewAttachments(pageNode) {
    if ( pageNode != null ) {
        var level = Number(pageNode.getAttribute("Level") );
        
        if ( LoadAttachments( (level ? pageNode.id : Number(pageNode.getAttribute("imgIndex") )), level ) )
            OpenDialog( "Attachments Viewer", "ViewAttachmentsDlg", 400, 400, OnViewAttachmentsDlgClose, OnCancelDlg2 );
    }
}

function OnDeleteTreeItem(pageNode) {
    if ( pageNode != null ) {
        var level = Number(pageNode.getAttribute("Level") );
        
        DeleteAttachments( (level ? pageNode.id : Number(pageNode.getAttribute("imgIndex") )), level );
        
        var tagA = pageNode.getElementsByTagName("A");    
        if ( tagA != null && tagA.parentNode != null ) {
            var imgAttach = getTreeViewPanel().document.getElementById(tagA.parentNode.id + "_attachimg");
            if ( imgAttach != null )
                tagA.parentNode.removeChild( imgAttach );
        }
    //tagA[0].style.fontWeight = "normal";
        
    }
}

function OnDownloadImage(pageNode) {
    if ( pageNode != null ) {
        var PDIControl = getControlPanel().oPDIControl;
        var imgIndex = Number(pageNode.getAttribute("imgIndex") );
        if ( PDIControl.DownloadPageByIndex( imgIndex ) ) {
            var path = PDIControl.GenerateThumbnail( imgIndex );
            if ( path != "" ) {
                pageNode.setAttribute( 'HasThumbnail', true );
                pageNode.setAttribute( 'FldImg', path );
                getTreeViewPanel().oIATree.SetFolderThumbnail( pageNode );

                var id = pageNode.getElementsByTagName("A")[0].getAttribute("id");
                var prevPageIndex = PDIControl.GetCurrentPageIndex();
                if(imgIndex == prevPageIndex)
                    PDIControl.ShowCurrentPage();
                PDIControl.SelectPageByIndex(imgIndex);
                RequestOCRDataForPage(
                    PDIControl.GetCurrentPage(),
                    id.substring(8),
                    imgIndex == prevPageIndex
                );
                PDIControl.SelectPageByIndex(prevPageIndex);
                return true;
            }
        }
    }

    return false;
}

function RequestOCRDataForPage(CurPage, id, showZones) {
    if ( CurPage != null) {
        PDITags = CurPage.GetTags();
        if ( PDITags ) {
            if ( oIABatch.GetIndexSettings().IsAllowRubberbandZone() ) {
                //load ocr zones for rubberband
                g_curIAValue = null;
                if ( getControlPanel().oIABatch.IsAdvancedIndexingMode() ) {
                    g_curIAValue = "";
                }
                else {
                    getControlPanel().g_IsXMLValue = true;
                    var request = INDEX_GET_OCR_ZONES_PREFIX +
                    "&id=" + oIABatch.GetProcessID() +
                    "&node=" + id +
                    "&instance=" + oIABatch.GetInstance() +
                    "&width="+PDITags.GetTag("PDI_TAG_WIDTH") +
                    "&height="+PDITags.GetTag("PDI_TAG_LENGTH")+
                    "&dpix="+PDITags.GetTag("PDI_TAG_RES_X") +
                    "&dpiy="+PDITags.GetTag("PDI_TAG_RES_Y");


                    http_getiavalue.sendGetRequest(request, GetIAValueCallback);
                }

                if ( g_curIAValue != null ) {
                    PDITags.SetTag(AddStatusPrefix(EINPUT_PAGE_OCR_ZONES), g_curIAValue );
                    if(showZones) {
                        oPDIControl.ShowOCRZones( g_curIAValue )
                    }
                }
                else
                    FailedOnLoadIndexData = true;
            }
        }
    }
}

function AsyncDownloadImage() {

    if ( getControlPanel().iAsyncDownloadImage ) {
        getControlPanel().clearTimeout( getControlPanel().iAsyncDownloadImage );
        getControlPanel().iAsyncDownloadImage = null;
    }
    
    getControlPanel().iAsyncDownloadImage = getControlPanel().setTimeout("getControlPanel().AsyncDownloadImage()", 100);
}

function LoadAttachments(index ,level) {
    //Load PDI documents
    var nCount = 0;
    
    try {
        var PDIControl = getControlPanel().oPDIControl;
        var oAttachmentsListContent = getControlPanelEl("AttachmentsList");
        
        var PDIDocument = PDIControl.GetDocument();
        if ( PDIDocument != null ) {
            var Tags = null;
            if ( level == 0 ) {
                var curPage = PDIDocument.GetPageByIndex(index);
                if ( curPage != null )
                    Tags = curPage.GetTags();
            }                
            else {
                Tags = PDIDocument.GetTags();
            }
            
            if ( Tags != null ) {
                var attachments = "";
                
                if ( level == 0 )
                    attachments = Tags.GetTag( AddStatusPrefix(EINPUT_PAGE_ATTACHMENTS) );
                else
                    attachments = Tags.GetTag( index + AddStatusPrefix(EINPUT_DOC_ATTACHMENTS) );
                
                if ( attachments != "" ) {
                    var arrAttachments = attachments.split("|");
                    var nAttachmentsCount = arrAttachments.length;
                    var AttachmentsList = "";
                    
                    AttachmentsList += "<SELECT style='border: 0px solid; width:362px' id='attachments' size='" + (nAttachmentsCount > 22 ? nAttachmentsCount : 22 ) + "' >";
                    
                    for ( var i = 0; i < nAttachmentsCount; i++ ) {
                        try {
                            var curAttachmentPage = PDIDocument.GetPageByIndex(Number(arrAttachments[i]));
                            if ( curAttachmentPage != null ) {
                                var AttachmentTags = curAttachmentPage.GetTags();
                                if ( AttachmentTags != null ) {
                                    AttachmentsList += "<option value='" + arrAttachments[i] + "'>";
                                    AttachmentsList += escapeHTML(AttachmentTags.GetTag("PDI_TAG_ORIGINAL_FILE_NAME"));
                                    AttachmentsList += "</option>";
                                    nCount++;
                                }
                            }
                        }
                        catch(e) {
                            ShowErrorAlert("Error occurred", e.name, e.number, e.description, oPDIControl.GetContext(), true);
                        }
                    }
                    
                    AttachmentsList += "</SELECT>";
                    
                    oAttachmentsListContent.innerHTML = AttachmentsList; 
                    var oAttachments = oAttachmentsListContent.document.getElementById("attachments");
                    oAttachments.multiple = false;
                    
                    //if ( oAttachments.offsetWidth <= oAttachmentsListContent.offsetWidth )
                    //   oAttachments.style.width = "100%";
                    
                    if ( level == 0 )
                        oAttachments.setAttribute("DocID", "");
                    else
                        oAttachments.setAttribute("DocID", index);
                }
            }
        }
    }
    catch(e) {
        ShowErrorAlert("Error occurred", e.name, e.number, e.description, PDIControl.GetContext(), true);
        return 0;
    }
    if ( !nCount )
        alert("No attachments to view");
    
    return nCount > 0;
}

function DeleteAttachments( id, level ) {
    var PDIControl = getControlPanel().oPDIControl;
    var PDIDocument = PDIControl.GetDocument();
    if ( PDIDocument != null ) {
        var Tags = null;
        if ( level == 0 ) {
            var curPage = PDIDocument.GetPageByIndex(id);
            if ( curPage != null )
                Tags = curPage.GetTags();
        }                
        else {
            Tags = PDIDocument.GetTags();
        }
        
        if ( Tags != null ) {
            var attachments = "";
            
            if ( level == 0 )
                attachments = Tags.GetTag( AddStatusPrefix(EINPUT_PAGE_ATTACHMENTS) );
            else
                attachments = Tags.GetTag( id + AddStatusPrefix(EINPUT_DOC_ATTACHMENTS) );
            
            if ( attachments != "" ) {
                var arrAttachments = attachments.split("|");
                var nAttachmentsCount = arrAttachments.length;

                if ( level == 0 )
                    Tags.SetTag( AddStatusPrefix(EINPUT_PAGE_ATTACHMENTS), "" );
                else
                    Tags.SetTag( id + AddStatusPrefix(EINPUT_DOC_ATTACHMENTS), "" );

                for ( var i = 0; i < nAttachmentsCount; i++ ) {
                    var index = Number(arrAttachments[i]);
                    var attPage = PDIDocument.GetPageByIndex(index);
                    if  ( attPage != null ) {
                        var attPageTags = attPage.GetTags();
                        var IsAttachment = (attPageTags.GetTag( AddStatusPrefix(EINPUT_IS_ATTACHMENT) ) == "true") ? true : false;
                        
                        if ( IsAttachment ) {
                            PDIDocument.DeletePageByIndex(index);
                            getTreeViewPanel().oIATree.UpdateImgIndexes(index);
							for(var j = 0; j < nAttachmentsCount; j++)
								if(Number(arrAttachments[j]) > Number(arrAttachments[i]))
									arrAttachments[j] = Number(arrAttachments[j]) - 1;
                        }
                    }
                }
                
            }
        }
    }
}

function OnDeleteAttachment() {
    var attachments = document.getElementById("attachments");
    if ( attachments.selectedIndex != -1 ) {
        var PDIControl = getControlPanel().oPDIControl;
        var index = attachments.options[attachments.selectedIndex].value;
        
        if ( DeleteAttachmentByIndex( index, attachments.getAttribute("DocID") ) ) {
            var oCurrentlyActiveItem = getTreeViewPanel().oIATree.GetCurrentlyActiveItem();
            if ( oCurrentlyActiveItem != null ) {
                var imgAttach = getTreeViewPanel().document.getElementById(oCurrentlyActiveItem.id + "_attachimg");
                if ( imgAttach != null )
                    oCurrentlyActiveItem.removeChild( imgAttach );
            //tagA[0].style.fontWeight = "normal";
            }
        }

		if(SAVE_AFTER_SCANNING) {
			getControlPanel().OnSaveBatch(false);
		}

    }
}

function DeleteAttachmentByIndex( index, DocID) {
    var PDIControl = getControlPanel().oPDIControl;
    var PDIDocument = PDIControl.GetDocument();
    if ( PDIDocument != null ) {
        var Tags = null;
        if ( DocID == "" ) {
            var curPage = PDIControl.GetCurrentPage();
            if ( curPage != null )
                Tags = curPage.GetTags();
        }                
        else {
            Tags = PDIDocument.GetTags();
        }
        
        if ( Tags != null ) {
            var attachmentsTag = "";
            
            if ( DocID == "" )
                attachmentsTag = Tags.GetTag( AddStatusPrefix(EINPUT_PAGE_ATTACHMENTS) );
            else
                attachmentsTag = Tags.GetTag( DocID + AddStatusPrefix(EINPUT_DOC_ATTACHMENTS) );
            
            if ( attachmentsTag != "" ) {
				var atts = attachmentsTag.split("|");
				for( i = 0; i < atts.length; i++ )
					if(atts[i] == index)
						atts.splice(i, 1);

                attachmentsTag = atts.join("|");
                
                if ( DocID == "" )
                    Tags.SetTag( AddStatusPrefix(EINPUT_PAGE_ATTACHMENTS), attachmentsTag );
                else
                    Tags.SetTag( DocID + AddStatusPrefix(EINPUT_DOC_ATTACHMENTS), attachmentsTag );
                
                try {
                    var attPage = PDIDocument.GetPageByIndex(Number(index));
                    if  ( attPage != null ) {
                        var attPageTags = attPage.GetTags();
                        var IsAttachment = (attPageTags.GetTag( AddStatusPrefix(EINPUT_IS_ATTACHMENT) ) == "true") ? true : false;
                        
                        if ( IsAttachment ) {
                            PDIDocument.DeletePageByIndex(Number(index));
                            getTreeViewPanel().oIATree.UpdateImgIndexes(index);
                        }
                    }
                }
                catch(e) {
                    ShowErrorAlert("Error occurred", e.name, e.number, e.description, PDIControl.GetContext(), true);
                }
            }
        }
    }
    
    var attachments = document.getElementById("attachments");
    attachments.removeChild( attachments.options[attachments.selectedIndex] );
    if ( attachments.options.length <= 0 ) {
        document.getElementById("btnViewAttachmentsCancel").click();
        return true;
    }
    else {
        for ( var i = 0; i < attachments.options.length; i++ ) {
            if ( Number(attachments.options[i].value) > index )
                attachments.options[i].value = attachments.options[i].value - 1;
        }
    }
    
    return false;
}

function OnViewAttachmentsDlgClose() {
    getControlPanel().oPDIControl.Show();
}


function OnAdvancedIndexing() {

	if(getTreeViewPanel().oIATree.GetPageCount() <= 0)
	{
		alert("The bach doesn't contain any pages");
		return;
	}


    if ( OnSaveBatch(true) )
        window.top.OpenIndexModule( getControlPanel().oPDIControl.GetDocument().GetGUID() );
}

function AdvIndexingCallback()
{
    if (!http_advindexing.processHttpResponseText("Unable to start advanced indexing!"))
		return;

    g_StartAdvIndexingFailed = http_advindexing.getResponseText().toLowerCase() != "true";
}

function OpenIndexModule( docid ) {

    //window.navigate("../" + INDEX_URL_PREFIX + "?docid=" + escape(docid));
    //set advanced indexing mode
    if ( navigator.onLine ) {
        g_StartAdvIndexingFailed = true;
        
        
        try {
            http_advindexing.sendGetRequest(RUN_ADVANCED_INDEXING, AdvIndexingCallback);
			ProcessLogout(http_advindexing);
        }
        catch(e) {
            alert("Error occurred! Description: " + e.description );
            g_StartAdvIndexingFailed = true;
        }
        if ( g_StartAdvIndexingFailed )
            return;
    }
    
    window.top.SkipLogout = true;
    window.top.clipboardData.setData("Text", "CURRENT_PDI_DOCUMENT"+docid+"/APPID"+window.top.GLOBAL_APP_GUID);
    window.top.navigate( INDEX_URL_PREFIX);
}

function OpenScanModule( docid ) {
    window.top.SkipLogout = true;
    window.top.clipboardData.setData("Text", "CURRENT_PDI_DOCUMENT"+docid+"/APPID"+window.top.GLOBAL_APP_GUID);
    window.top.navigate("../" + SCAN_URL_PREFIX);
}

function OnNoneAnnotation() {
    getControlPanel().oPDIControl.DisableStickyMode();
    getControlPanel().oPDIControl.SelectAnnotationTool(11); //none
}

function OnDownLoadImages() {

    oPDIControl = getControlPanel().oPDIControl;
    var PDIDocument = oPDIControl.GetDocument();
    
    if ( PDIDocument != null ) {
        var i = 0;
        var pageCount = PDIDocument.GetPageCount();
        
        for ( ; i < pageCount; i++ ) {
            var oPage = PDIDocument.GetPageByIndex( i );
            if ( !oPage.IsPageOperable() ) {
                if ( getTreeViewPanel().oIATree != null ) {
                    var NodeItem = getTreeViewPanel().oIATree.GetNodeByImageIndex(i);
                    if ( NodeItem != null ) {
                        if ( !getControlPanel().OnDownloadImage( NodeItem ) )
                            return;
                        else
                            break;
                    }                  
                }
            }
        }
        
        if ( i < pageCount ) {
            if ( getControlPanel().iDownLoadImages ) {
                getControlPanel().clearTimeout( getControlPanel().iDownLoadImages );
                getControlPanel().iDownLoadImages = null;
            }
            
            getControlPanel().iDownLoadImages = getControlPanel().setTimeout("getControlPanel().OnDownLoadImages()", 10);
        }
    }
}


function CancelIndexTask(DocId) {
    try {
		var prefix  = INDEX_CANCEL_TASK;
		if(getUIMode() == RESCAN_MODE)
			prefix = RESCAN_CANCEL_TASK;

        http_cancelIndexTask.sendGetRequest(prefix + '&id='+escape(DocId), handleCancelIndexTask);
		ProcessLogout(http_cancelIndexTask);
        
        if ( g_CancelIndexTaskFailed ) {
            alert("This task will be canceled automatically by eInput server");
            g_CancelIndexTaskFailed = true;
        }
    }
    catch(e) {
        ShowErrorAlert("Error occurred ", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }
}

function handleCancelIndexTask()
{
	if(!http_cancelIndexTask.processHttpResponseText("Unable to cancel task!"))
		return;
	
    g_CancelIndexTaskFailed = ( http_cancelIndexTask.getResponseText().toLowerCase() != "true" );
}

function TerminateValidation() {
    oPDIControl = getControlPanel().oPDIControl;
    try {
        if ( !navigator.onLine )
            return;
		
        http_TerminateValidation.sendGetRequest(INDEX_TERMINATE_VALIDATION);
    }
    catch(e) {
        ShowErrorAlert("Error occurred ", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }
}

function CommitIndexDoc() {

    oPDIControl = getControlPanel().oPDIControl;
    try {
        if ( !navigator.onLine )
            return null;
        
        //try to commit successfully downloaded PDI document
        
        var PDIDocument = oPDIControl.GetDocument();
        
        g_CommitIndexDocFailed = null;
        
		var prefix = INDEX_COMMIT_DOC;
		if(getUIMode() == RESCAN_MODE )
			prefix = RESCAN_COMMIT_DOC;

        http_commitIndexDoc.sendGetRequest(prefix + '&id=' + PDIDocument.GetGUID(), OnCommitIndexDoc);
        ProcessLogout(http_commitIndexDoc);

        return !g_CommitIndexDocFailed;
    }
    catch(e) {
        ShowErrorAlert("Error occurred ", e.name, e.number, e.description, oPDIControl.GetContext(), true);
    }
    return null;
}


function OnCommitIndexDoc() {
	if (!http_commitIndexDoc.processHttpResponseText("Unable to commit current task on eInput server!"))
		return false;

	g_CommitIndexDocFailed = ( http_commitIndexDoc.getResponseText().toLowerCase() != "true" );
}

function showLoginInfo()
{
    var iLeft = 0;
    var iTop = 0;
    var oNode = getControlPanelEl("arrows");

    while (oNode != getControlPanel().document.body) {
        iLeft += oNode.offsetLeft;
		iTop += oNode.offsetTop;
        oNode = oNode.offsetParent;
    }

	var editServerInfo =
		window.top.isSSOEnabled() &&
		window.top.GLOBAL_SSO_USER_CAN_CHANGE_SERVER &&
		navigator.onLine && (getUIMode() != INDEX_MODE || window.top.AdvancedIndexingDocID == "");

	getControlPanelEl("SERVERPOPUP").disabled = !editServerInfo;
	getControlPanelEl("DEPARTMENTPOPUP").disabled = !editServerInfo;
	getControlPanelEl("changebutton").style.display = editServerInfo ? "block" : "none";

	getControlPanelEl("arrows").src = "../graphics/arrowdown.gif";

	if(window.top.isSSOEnabled())
	{
		getControlPanelEl("SERVERPOPUP").value = window.top.GLOBAL_SSO_SERVER;
		getControlPanelEl("DEPARTMENTPOPUP").value = window.top.GLOBAL_SSO_DEPARTMETS;
		window.top.showPopWinXY(getControlPanelEl("InputAccelServer"), "Change Server", 0, iTop+15, 255, 80, changeServer, ChangeArrow, true);
	}
	else
	{
		window.top.showPopWinXY(getControlPanelEl("InputAccelServer"), "Connection Info", 0, iTop+15, 255, 80, ChangeArrow, ChangeArrow, true);
	}
}

function ChangeArrow()
{
	getControlPanelEl("arrows").src = "../graphics/arrowright.gif";
}

function changeServer()
{
	if(document.getElementById( 'BatchPanel' ) == null || document.getElementById( 'BatchPanel' ).style.display == "none" || //Batch actions are hidden
			OnCloseBatch(false, false))
		PerformChangeServer();
}

function PerformChangeServer()
{

    var server      = "";
    var department	= "";

    // ignore request if we are already logged in
    var oPopupFrame = document.getElementById("popupFrame");

    var inputControls = oPopupFrame.getElementsByTagName("INPUT");
    for ( var n = 0; n < inputControls.length; n++ )
    {
        switch ( inputControls[n].id )
        {
            case "SERVERPOPUP":
                server = inputControls[n].value;
                break;
            case "DEPARTMENTPOPUP":
                department = inputControls[n].value;
                break;
        }
    }


//	window.top.GLOBAL_SSO_DEPARTMETS = department;
//	window.top.GLOBAL_SSO_SERVER = server;


	var prefix = window.top.g_Module.getChangeSrvPrefix()+
		"department="+department+
		"&server="+server;

	http_changeserver.server = server;
	http_changeserver.dep = department;

	http_changeserver.sendGetRequest(prefix, changeServerCallback);
	ProcessLogout(http_changeserver);
}

function changeServerCallback()
{
	if (!http_changeserver.processHttpResponseText("Changing of connection settings failed!"))
		return;

	if (http_changeserver.getResponseText() != "false")
	{
		window.top.SESSION_ID = http_changeserver.getResponseText();
		SetCookie(EINPUT_SESSION_ID, http_changeserver.getResponseText());

		window.top.GLOBAL_SSO_SERVER = http_changeserver.server;
		window.top.GLOBAL_SSO_DEPARTMETS = http_changeserver.dep;


		loggedIn = true;

		initLoginInfo();

		window.top.hidePopWinXY(false);
		ChangeArrow();

	}
}

//IMAGE ACCESS MOD

function setCustomFields()
{
	var v = window.top.frames["IndexFieldsPanelFrame"].g_objValidator;
	pageCount = getValFromXML(v.GetIndexFieldsXML(), "index_field12");
	docCount = getValFromXML(v.GetIndexFieldsXML(), "index_field13");
	
	var t = oPDIControl.GetDocument().GetTags();
	t.SetTagDirectly("pageCount", pageCount);
	t.SetTagDirectly("docCount", docCount);
}

function validateBatchStructure()
{
	var errorMessage = "";
	
	var folderNodes = getFolderNodes();
	var docCount = 0;
	var pageCount = 0;
	
	for(var i = 0; i < folderNodes.length; ++i)
	{
		var docNodes = folderNodes.item(i).children.item(3).children;
		
		docCount += docNodes.length;
		
		for(var j = 0; j < docNodes.length; ++j)
		{
			pageCount += docNodes.item(j).children.item(3).children.length;			
		}
	}
	

	var docTags = oPDIControl.GetDocument().GetTags();
	
	var expectedPageCount = parseInt(docTags.GetTag("pageCount"));
	var expectedDocCount = parseInt(docTags.GetTag("docCount"));

	if(isNaN(expectedPageCount))
		expectedPageCount = 0;
	if(isNaN(expectedDocCount))
		expectedDocCount = 0;

	
	if(expectedPageCount != pageCount)
	{
		errorMessage = "Page count incorrect. " + pageCount + ((pageCount == 1) ? " page is " : " pages are ") + "in batch, " + expectedPageCount + ((expectedPageCount == 1) ? " page is " : " pages are ") + "expected.  ";
	}
	
	if(expectedDocCount != docCount)
	{
		errorMessage += "Document count incorrect. " + docCount + ((docCount == 1) ? " document is " : " documents are ") + "in batch, " + expectedDocCount + ((expectedDocCount == 1) ? " document is " : " documents are ") + "expected.";
	}
	
	return errorMessage;
}

/**
 *	Retrieves a reference to the folder nodes (coversheets) of the tree stucture
 **/
function getFolderNodes()
{
	var text = getTreeViewPanel().oIATree.GetCurrentlyActiveItem().outerText;
	
	if(text.substring(0, 4) == "GDO-")
	{
		return getTreeViewPanel().oIATree.GetCurrentlyActiveItem().children.item(3).children;
	}
	else if(text.substring(0, 3) == "GDO")
	{
		return getTreeViewPanel().oIATree.GetCurrentlyActiveItem().parentElement.children;
	}
	else if(text.substring(0, 5) == "Patch")
	{
		return getTreeViewPanel().oIATree.GetCurrentlyActiveItem().parentElement.parentElement.parentElement.children;
	}
	else	//page
	{
		return getTreeViewPanel().oIATree.GetCurrentlyActiveItem().parentElement.parentElement.parentElement.parentElement.parentElement.children;
	}
}

/**
*	Parses barcode value out of XML string for document node
**/
function getValFromXML(xml, key)
{
	var val;

	if (window.DOMParser)
	{
		parser=new DOMParser();
		xmlDoc=parser.parseFromString(xml,"text/xml");
	}
	else // Internet Explorer
	{
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async="false";
		xmlDoc.loadXML(xml);
	}

	var x = xmlDoc.getElementsByTagName("index_item");
	for(var i = 0; i < x.length; i++)
	{
		if(x[i].getElementsByTagName("id")[0].childNodes[0].nodeValue == key)
		{
			val = x[i].getElementsByTagName("value")[0].childNodes[0].nodeValue;
			break;
		}
	}
	return val;   
}


//IMAGE ACCESS MOD - END