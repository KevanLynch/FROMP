/*   1:    */ package Prog;
/*   2:    */ 
/*   3:    */ import Objects.Project;
/*   4:    */ import Objects.Sample;
/*   5:    */ import Panes.ActMatrixPane;
/*   6:    */ import Panes.PathwayActivitymatrixPane;
/*   7:    */ import Panes.PathwayMatrix;
/*   8:    */ import java.awt.Color;
/*   9:    */ import java.awt.Dimension;
/*  10:    */ import java.io.BufferedReader;
/*  11:    */ import java.io.File;
/*  12:    */ import java.io.FileReader;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.util.ArrayList;
/*  15:    */ 
			//this controls the cmdFromp functions

/*  16:    */ public class CmdController
/*  17:    */ {
/*  18:    */   public static String[] args_;
/*  19:    */   String inputPath_;
/*  20:    */   String outPutPath_;
/*  21:    */   String optionsCmd_;
				String ec_;
/*  22:    */   static Controller controller;
/*  23:    */   static PathwayMatrix pwMAtrix;
				final String basePath_ = new File(".").getAbsolutePath() + File.separator;
/*  24:    */   
/*  25:    */   public CmdController(String[] args)
/*  26:    */   {
/*  27: 30 */     System.out.println("Starting cmdFromp");
/*  28:    */     
/*  29: 32 */     args_ = args;
/*  30: 33 */     this.inputPath_ = getInputPath();
/*  31: 34 */     System.out.println("input: " + this.inputPath_);
/*  32: 35 */     this.outPutPath_ = getOutputPath();
/*  33: 36 */     System.out.println("output: " + this.outPutPath_);
/*  34: 37 */     this.optionsCmd_ = getOptionCmd();
/*  35: 38 */     System.out.println("option: " + this.optionsCmd_);
				  if(args_.length==4){
				  	this.ec_ = getEC();
/*  35: 38 */     	System.out.println("ec: " + this.ec_);
				  }
/*  36: 39 */     controller = new Controller(Color.black);
/*  37:    */     
/*  38: 41 */     Panes.Loadingframe.showLoading = false;
/*  39: 42 */     Panes.HelpFrame.showSummary = false;
/*  40: 44 */     if (this.inputPath_.endsWith(".frp"))//if the input file is of the the project file type, load the project
/*  41:    */     {
/*  42: 45 */       controller.loadProjFile(this.inputPath_);
/*  43:    */     }
/*  44: 47 */     else if (this.inputPath_.endsWith(".out"))//if the input file if of the sample file type, create a new sample with the file, and add that sample to a new project
/*  45:    */     {
/*  46: 49 */       String name = this.inputPath_.substring(this.inputPath_.lastIndexOf(File.separator));
/*  47: 50 */       Sample sample = new Sample(name, this.inputPath_, Color.red);
/*  48:    */       
/*  49: 52 */       Project.samples_.add(sample);
/*  50:    */     }
/*  51: 54 */     else if (this.inputPath_.endsWith(".lst"))//if the input file is of the .lst type, itterate through the file and build samples for all of the file paths in the file, if the line starts with <userP> a new userpath is added. They are all added to a new project
/*  52:    */     {
/*  53:    */       try
/*  54:    */       {
/*  55: 56 */         BufferedReader in = new BufferedReader(new FileReader(this.inputPath_));
/*  56:    */         
/*  57: 58 */         String comp = "<userP>";
/*  58:    */         String line=in.readLine();
					  System.out.println("Entering while loop");
/*  59: 60 */         while ((line) != null)
/*  60:    */         {
						System.out.println("Looping");
						try{
							//String line;
/*  62: 61 */           	if (((line) != null) && line.startsWith(comp))
/*  63:    */          		{
/*  64: 62 */           	  String userP = line.substring(comp.length());
							  System.out.println("user pathway made");
/*  65: 63 */           	  Project.addUserP(userP);
							  System.out.println("pathway added");
/*  66:    */           	}
/*  67:    */           	else if ((line) != null) 
/*  68:    */           	{
							  File f=new File (line);
							  if (f.exists() && !f.isDirectory()){
/*  69: 66 */           	  	String name = line.substring(line.lastIndexOf(File.separator));
							  	System.out.println("substring");
/*  70: 67 */           	  	Color col = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
/*  71: 68 */           	  	Sample sample = new Sample(name, line, col);
/*  72:    */           	  	
/*  73: 70 */           	  	Project.samples_.add(sample);
							  	System.out.println("sample added");
							  }
							  else{System.out.println("file does not exist");}

/*  74:    */           	}

						}
						catch (Exception e)
/*  78:    */       	{
/*  79: 74 */       	  e.printStackTrace();
						  continue;
/*  80:    */       	}
/*  61:    */           line=in.readLine();
/*  75:    */         }
/*  76:    */       }
/*  77:    */       catch (Exception e)
/*  78:    */       {
/*  79: 74 */         e.printStackTrace();
/*  80:    */       }
/*  81:    */     }
				  else{//anything other than .frp, .out, or .lst are not accepted
				  	System.out.println("Incorrect File Format");
				  	System.exit(1);//exits with an error
				  }
/*  82: 77 */     Controller.loadPathways(true);
				  if((args_.length==4)&&(this.args_[3]!=null)){//this takes args[3] (ie. the ec name inputted) and exports the sequence IDs for all samples for that EC
				  	ActMatrixPane pane = new ActMatrixPane(Controller.project_, DataProcessor.ecList_, Controller.processor_, new Dimension(12, 12));
				  	System.out.println("Repseqs will be saved at: "+basePath_+"RepSeqIDs/");
					pane.cmdExportRepseqs(this.ec_);
				  }
/*  83: 78 */     if ((this.optionsCmd_.contentEquals("p")) || (this.optionsCmd_.contentEquals("op")) || (this.optionsCmd_.contentEquals("up")) || (this.optionsCmd_.contentEquals("a")))
/*  84:    */     {//this if statment contains al of the picture export commands
/*  85: 79 */       System.out.println("Pathway-score-matrix");
/*  86: 80 */       pwMAtrix = new PathwayMatrix(Project.samples_, Project.overall_, DataProcessor.pathwayList_, Controller.project_);//builds a pathway matrix object whigh will be used to generate the pathway pictures
/*  87: 81 */       if (this.optionsCmd_.contentEquals("a"))
/*  88:    */       {//if the a command is selected export all of the pathway pictures
/*  89: 82 */         String tmpPAth = this.outPutPath_ + File.separator + "pathwayPics";
/*  90: 83 */         System.out.println("PathwayPics will be saved at: " + tmpPAth);
/*  91: 84 */         pwMAtrix.exportPics(tmpPAth, false, false);
/*  92:    */       }
/*  93: 87 */       else if (this.optionsCmd_.contentEquals("p"))
/*  94:    */       {//if the p command is selected export all of the pathway pictures, then exit
					  String tmpPAth = this.outPutPath_ + File.separator + "pathwayPics";
/*  90: 83 */         System.out.println("PathwayPics will be saved at: " + tmpPAth);
/*  95: 88 */         pwMAtrix.exportPics(tmpPAth, false, false);
/*  96: 89 */         System.exit(0);
/*  97:    */       }
/*  98: 91 */       else if (this.optionsCmd_.contentEquals("op"))
/*  99:    */       {// if the op command is selected export all the multi pathway pictures, then exit
					  String tmpPAth = this.outPutPath_ + File.separator + "multiPathwayPics";
/*  90: 83 */         System.out.println("PathwayPics will be saved at: " + tmpPAth);
/* 100: 92 */         pwMAtrix.exportPics(tmpPAth, true, false);//note the true in the exportPics call. There was no false in the previous calls. This is how it differs
/* 101: 93 */         System.exit(0);
/* 102:    */       }
/* 103: 95 */       else if (this.optionsCmd_.contentEquals("up"))
/* 104:    */       {//if the up command is selected then export only the user pathway pictures, then exit 
					  String tmpPAth = this.outPutPath_ + File.separator + "userPathwayPics";
/*  90: 83 */         System.out.println("PathwayPics will be saved at: " + tmpPAth);
/* 105: 96 */         System.out.println("onlyUserPaths");
/* 106: 97 */         pwMAtrix.exportPics(tmpPAth, true, true);//both are true here
/* 107: 98 */         System.exit(0);
/* 108:    */       }
/* 109:101 */       if (this.optionsCmd_.contentEquals("p")) {
/* 110:102 */         System.exit(0);
/* 111:    */       }
/* 112:    */     }
/* 113:105 */     if ((this.optionsCmd_.contentEquals("s")) || (this.optionsCmd_.contentEquals("a")) || (this.optionsCmd_.contentEquals("am")))
/* 114:    */     {//exports the pathway score matrix for the corresponding commands. if s is selected it exits afterwards
/* 115:106 */       System.out.println("Pathway-score-matrix");
/* 116:107 */       pwMAtrix = new PathwayMatrix(Project.samples_, Project.overall_, DataProcessor.pathwayList_, Controller.project_);
/* 117:108 */       pwMAtrix.exportMat(this.outPutPath_, true);
/* 118:109 */       if (this.optionsCmd_.contentEquals("s")) {
/* 119:110 */         System.exit(0);
/* 120:    */       }
/* 121:    */     }
/* 122:113 */     if ((this.optionsCmd_.contentEquals("m")) || (this.optionsCmd_.contentEquals("a")) || (this.optionsCmd_.contentEquals("am")))
/* 123:    */     {//exports the pathway activity matrix for the corresponding commands. if m is selected it exits afterwards
/* 124:114 */       System.out.println("Pathway-activity-matrix");
/* 125:115 */       PathwayActivitymatrixPane pane = new PathwayActivitymatrixPane(Controller.processor_, Controller.project_, new Dimension(23, 23));
/* 126:116 */       pane.exportMat(false, this.outPutPath_);
/* 127:117 */       if (this.optionsCmd_.contentEquals("m")) {
/* 128:118 */         System.exit(0);
/* 129:    */       }
/* 130:    */     }
/* 131:121 */     if ((this.optionsCmd_.contentEquals("e")) || (this.optionsCmd_.contentEquals("a"))  || (this.optionsCmd_.contentEquals("am")))
/* 132:    */     {//exports the EC-activity matrix for the corresponding commands. if e is selected it exits afterwards
/* 133:122 */       System.out.println("EC-activity-matrix");
/* 134:123 */       ActMatrixPane pane = new ActMatrixPane(Controller.project_, DataProcessor.ecList_, Controller.processor_, new Dimension(12, 12));
/* 135:124 */       pane.exportMat(this.outPutPath_, true);
//					if((args_.length==4)&&(this.args_[3]!=null)){
//						System.out.println("Repseqs will be saved at: "+basePath_+"RepSeqIDs/");
//						pane.cmdExportRepseqs(this.ec_);
//					}
/* 136:125 */       if (this.optionsCmd_.contentEquals("e")) {
/* 137:126 */         System.exit(0);
/* 138:    */       }
/* 139:    */     }
				  
/* 140:129 */     System.exit(0);
/* 141:    */   }
/* 142:    */   
/* 143:    */   private String getInputPath()
/* 144:    */   {
/* 145:134 */     return args_[0];
/* 146:    */   }
/* 147:    */   
/* 148:    */   private String getOutputPath()
/* 149:    */   {
/* 150:137 */     return args_[1];
/* 151:    */   }
/* 152:    */   
/* 153:    */   private String getOptionCmd()
/* 154:    */   {
/* 155:140 */     return args_[2];
/* 156:    */   }
				private String getEC()
				{
				  return args_[3]; 
				}
/* 157:    */ }