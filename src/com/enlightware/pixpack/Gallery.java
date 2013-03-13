package com.enlightware.pixpack;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Root;
import com.enlightware.pixpack.Server;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.exception.XMLException;
import com.laukien.string.Convert;
import com.laukien.string.Cut;
import com.laukien.string.HTML;
import com.laukien.string.Random;
import com.laukien.string.Replace;
import com.laukien.taglib.i18n.Text;
import com.laukien.xml.XML;

public class Gallery extends Root {
	private static final String SUBFOLDER=" OR (key_user IN (SELECT id FROM account WHERE key_parent=?) AND file.key_folder IN (SELECT id FROM FOLDER WHERE name=?))";

	private int gId;
	private String gTimestamp;
	private String gKey;
	private int gPosition;
	private int gLast;
	private String gTemplate;
	private String gStyle;
	private String gUser;
	private String gFolder;
	private String gTitle;
	private String gDescription;
	private int gCount;
	private short gCol;
	private short gRow;
	private boolean gIsSubfolder;
	private String gSortBy;
	private String gSortOrder;
	private String gCSS;

	public Gallery() {
		super();
		gId=-1;
		gTimestamp=null;
		gKey=null;
		gPosition=1;
		gLast=-1;
		gTemplate=null;
		gStyle=null;
		gUser=null;
		gFolder=null;
		gTitle=null;
		gDescription=null;
		gCount=-1;
		gCol=gRow=-1;
		gIsSubfolder=false;
		gSortBy="name";
		gSortOrder="ASC";
		gCSS=null;
	}
	
	public HttpServletRequest getRequest() {
		return gRequest;
	}
	
	/**
	 * Trys to get "i18n".
	 * 
	 * @return i18n-Object or <code>null</code>
	 */
	public Text getI18n() {
		if(gI18n==null) {
			if(gRequest==null) return null;
			gI18n=Lib.getI18n(gRequest);
		}
		return gI18n;
	}
	
	public int getId() {
		return gId;
	}
	
	public void setTimestamp(String pTimestamp) {
		gTimestamp=pTimestamp;
	}

	public String getTimestamp() {
		return gTimestamp;
	}
	
	public void setKey(String pKey) {
		gKey=pKey;
	}
	
	public String getKey() {
		return gKey;
	}
	
	public void setPosition(int pPosition) {
		gPosition=pPosition;
	}
	
	public int getPosition() {
		return gPosition;
	}
	
	public int getLast() {
		return gLast;
	}
	
	public void setTemplate(String pTemplate) {
		if(pTemplate==null) {
			gTemplate=null;
			gStyle=null;
			return;
		}
		
		int dot=pTemplate.indexOf('.');
		if(dot==-1) {
			//No style
			gTemplate=pTemplate;
			gStyle=null;
		} else {
			//with style
			gTemplate=pTemplate.substring(0,dot);
			gStyle=pTemplate.substring(dot+1);
		}
	}
	
	public String getTemplate() {
		return gTemplate;
	}
	
	public void setStyle(String pStyle) {
		gStyle=pStyle;
	}
	
	public String getStyle() {
		return gStyle;
	}
	
	public String getUser() {
		return gUser;
	}
	
	public void setFolder(String pFolder) {
		gFolder=pFolder;
	}
	
	public String getFolder() {
		return gFolder;
	}
	
	public void setTitle(String pTitle) {
		if(pTitle==null || (pTitle=pTitle.trim()).length()==0) gTitle=null;
		else gTitle=HTML.isTag(pTitle) ? null : pTitle;
	}
	
	public String getTitle() {
		return gTitle;
	}
	
	public void setDescription(String pDescription) {
		if(pDescription==null || (pDescription=pDescription.trim()).length()==0) gDescription=null;
		else gDescription=HTML.isValidText(pDescription) ?  pDescription : null;
	}
	
	public String getDescription() {
		return gDescription;
	}
	
	public int getCount() {
		return gCount;
	}
	
	public void setCol(short pCol) {
		gCol=pCol>0 ? pCol : -1;
	}

	public int getCol() {
		return gCol;
	}
	
	public void setRow(short pRow) {
		gRow=pRow>0 ? pRow : -1;
	}
	
	public int getRow() {
		return gRow;
	}
	
	public void setSortOrder(boolean pOrder) {
		gSortOrder=pOrder ? "DESC" : "ASC";
	}

	public boolean isSortOrder() {
		return gSortOrder.charAt(0)=='D';
	}
	
	public void setSortByTimestamp(boolean pSet) {
		gSortBy=pSet ? "timestamp" : "name";
	}

	public boolean isSortByTimestamp() {
		return gSortBy.charAt(0)=='t';
	}
	
	public void setSubfolder(boolean pInclude) {
		gIsSubfolder=pInclude;
	}
	
	public boolean isSubfolder() {
		return gIsSubfolder;
	}
	
	public void checkId() {
		if(gPosition<1) gPosition=1;
		if(gTimestamp==null || gTimestamp.length()!=17 || gKey==null || gKey.length()!=10) throw new ParameterException("Gallery.checkId: Timestamp or/and Key not set");
	}
	
	public boolean getGalleryById(int pId) {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//get galelry from db
			ps=db.prepareStatement("SELECT " +
					"gallery.id, gallery.title, gallery.description, gallery.timestamp, gallery.key, " +
					"folder.name AS folder, (SELECT name FROM account WHERE id=folder.key_user) AS folder_user, " +
					"(SELECT count(id) FROM file WHERE file.key_folder=gallery.key_folder AND file.public=true) AS last, " +
					"template.name AS template, " +
					"gallery.style, gallery.count, gallery.col, gallery.row, gallery.sort_timestamp, gallery.sort_order, gallery.subfolder " +
					"FROM gallery, folder, template " +
					"WHERE " +
					"gallery.id=? AND " +
					"gallery.key_folder=folder.id AND " +
					"gallery.key_template=template.id");
			ps.setInt(1,pId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) return false;

			//set variables
			gId=rs.getInt("id");
			gTitle=rs.getString("title");
			gDescription=rs.getString("description");
			gTimestamp=rs.getString("timestamp");
			gKey=rs.getString("key");
			gFolder=rs.getString("folder");
			gUser=rs.getString("folder_user");
			gTemplate=rs.getString("template");
			gStyle=rs.getString("style");
			gCount=rs.getInt("count");
			gLast=rs.getInt("last");
			gCol=rs.getShort("col");
			gRow=rs.getShort("row");
			setSortByTimestamp(rs.getBoolean("sort_timestamp"));
			setSortOrder(rs.getBoolean("sort_order"));
			gIsSubfolder=rs.getBoolean("subfolder");
			
			return true;
		} catch(Exception e) {
			Log.write("Gallery.getGalleryById: Database-Error\n"+e,Log.SYSTEM);
			return false;
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
	}
	
	public String getList() {
		checkRequest();
		StringBuffer sb=new StringBuffer();
		int count=-1;

		String query="SELECT gallery.id, gallery.title, gallery.timestamp, gallery.key, folder.name AS folder, template.name AS template, gallery.count " +
				"FROM gallery, folder, template " +
				"WHERE folder.key_user=? AND gallery.key_folder=folder.id AND gallery.key_template=template.id ";
		
		//extend the SQL-query
		int offset;
		try {
			offset=Integer.parseInt(gRequest.getParameter("offset"));
			gRequest.getSession().setAttribute("gallery.offset", new Integer(offset));
		} catch(Exception e) {
			try {
				offset=((Integer)gRequest.getSession().getAttribute("gallery.offset")).intValue();
			} catch(Exception e1) {
				offset=0;
			}
		}

		String order=gRequest.getParameter("order");
		boolean direction;
		try {
			direction=((Boolean)gRequest.getSession().getAttribute("gallery.direction")).booleanValue();
		} catch(Exception e) {
			direction=false;
		}
		
		if(com.laukien.string.String.isEmpty(order) || (!order.equals("title") && !order.equals("folder") && !order.equals("template") && !order.equals("count"))) {
			order=(String)gRequest.getSession().getAttribute("gallery.order");
			if(order==null) order="title";
		} else {
			if(order.equals((String)gRequest.getSession().getAttribute("gallery.order"))) {
				direction=!direction;
				gRequest.getSession().setAttribute("gallery.direction",new Boolean(direction));
			}
			gRequest.getSession().setAttribute("gallery.order",order);
		}

		
		query+="ORDER BY "+order+" "+(direction ? "DESC" : "ASC") +" OFFSET "+offset+" LIMIT 20";

		//init login
		Login login=new Login();
		login.setRequest(gRequest);
		int userId=login.getUser().getId();

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(gallery.id) AS count FROM gallery, folder, template WHERE folder.key_user=? AND gallery.key_folder=folder.id AND gallery.key_template=template.id");
			ps.setInt(1,userId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Gallery.getList: Invalid Result");
			count=rs.getInt("count");
			
			//Remove fiels form the filesystem
			ps=db.prepareStatement(query);
			ps.setInt(1,userId);
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Gallery.getList: Invalid Result");
			
			int row=0;
			String id;
			while(rs.next()) {
				row++;
				id=rs.getString("timestamp")+"','"+rs.getString("key");
				sb.append("<tr class=\"row"+((row%2)+1)+"\">");
				sb.append("<td>"+(rs.getString("title")!=null ? Cut.length(rs.getString("title"),100,"...") : "???"+rs.getString("folder")+"???")+"</td>");
				sb.append("<td>"+rs.getString("folder")+"</td>");
				sb.append("<td>"+Cut.length(rs.getString("template"),100,"...")+"</td>");
				sb.append("<td>"+rs.getString("count")+"</td>");
				sb.append("<td class=\"nobr\">");
				sb.append("<a href=\"javascript:gallery_show('"+id+"')\"><img alt=\"show\" src=\"/image/icon/show16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"javascript:gallery_hotlink('"+id+"')\"><img alt=\"hotlink\" src=\"/image/icon/hotlink16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"javascript:gallery_inlet('"+id+"')\"><img alt=\"inlet\" src=\"/image/icon/inlet16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"gallery.edit.html?id="+rs.getInt("id")+"\"><img alt=\"edit\" src=\"/image/icon/edit16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"gallery.delete.html?id="+rs.getInt("id")+"\"><img alt=\"delete\" src=\"/image/icon/delete16.gif\" border=\"0\"/></a>");
				sb.append("</td></tr>");
			}
		} catch(Exception e) {
			Log.write("Gallery.getList: Database-Error\n"+e,Log.SYSTEM);
			
			//delete variables
			gRequest.getSession().removeAttribute("gallery.offset");
			gRequest.getSession().removeAttribute("gallery.order");
			gRequest.getSession().removeAttribute("gallery.direction");
			
			return "<tr><td colspan=\"5\">Permission-Database<br/>"+e+"</td></tr>";
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
		
		//Navigation
		if(count>20) {
			sb.append("<tr><td colspan=\"5\">");
			sb.append(Lib.next(count, offset, 20, null, null));
			sb.append("</td></tr>");
		}
		
		return sb.toString();
	}
	
	public boolean add() {
		//check parameters
		if(gFolder==null || (gFolder=gFolder.trim()).length()==0 || gTemplate==null) return false;
		
		//check if template.style@parentId exists
			
		Login login=new Login(gRequest);
		int userId=login.getUser().getId();
		int parentId=login.getParentId();
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the template exists and its permission
			if(parentId<Account.BUSINESS_ID) {
				ps=db.prepareStatement("SELECT count(id) FROM template WHERE name=? AND styles "+(gStyle!=null ? "LIKE ?" : "IS NULL"));
				ps.setString(1, gTemplate);
				if(gStyle!=null) {
					ps.setString(2, "%|"+gStyle+"|%");
				}
			} else {
				ps=db.prepareStatement("SELECT count(id) FROM template WHERE (key_parent=1 OR key_parent=?) AND (type=0 OR type=?) AND name=? AND styles "+(gStyle!=null ? "LIKE ?" : "IS NULL"));
				ps.setInt(1,parentId);
				ps.setInt(2,Template.TYPE_GALLERY);
				ps.setString(3, gTemplate);
				if(gStyle!=null) {
					ps.setString(4, "%|"+gStyle+"|%");
				}
			}
			
			rs=ps.executeQuery();

			if(rs==null || !rs.next() || rs.getInt("count")!=1) throw new DatabaseException("Gallery.add: Invalid Result (count)");
			
			//add gallery
			//Write intio the database - if there is any hack an exception will be thrown --> error-message
			ps=db.prepareStatement("INSERT INTO gallery (title, description, col, row, sort_timestamp, sort_order, subfolder, style, timestamp, key, key_folder, key_template) VALUES (?,?,?,?,?,?,?,?,?,?,(SELECT id FROM folder WHERE name=? AND key_user=?),(SELECT id FROM template WHERE name=?))");
			ps.setString(1, gTitle);
			ps.setString(2, gDescription);
			ps.setShort(3, gCol);
			ps.setShort(4, gRow);
			ps.setBoolean(5, isSortByTimestamp());
			ps.setBoolean(6, isSortOrder());
			ps.setBoolean(7, gIsSubfolder);
			ps.setString(8, gStyle);
			ps.setString(9, DateTime.getTimestamp());	//timestamp
			ps.setString(10, Random.random(10,'a','z'));	//key
			ps.setString(11, gFolder);
			ps.setInt(12, userId);
			ps.setString(13, gTemplate);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Gallery.add: Database-Error\n"+e,Log.SYSTEM);
			return false;
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
		
		return true;
	}
	
	public boolean edit() {
		if(gId==-1) return false;

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("UPDATE gallery set title=?, description=?, col=?, row=?, sort_timestamp=?, sort_order=? WHERE id=?");
			ps.setString(1, gTitle);
			ps.setString(2, gDescription);
			ps.setInt(3, gCol);
			ps.setInt(4, gRow);
			ps.setBoolean(5, isSortByTimestamp());
			ps.setBoolean(6, isSortOrder());
			ps.setInt(7, gId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Gallery.edit: Database-Error\n"+e,Log.SYSTEM);
			return false;
		} finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
		
		return true;
	}
	
	public boolean delete() {
		if(gId==-1) return false;
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("DELETE FROM gallery WHERE id=?");
			ps.setInt(1, gId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Gallery.delete: Database-Error\n"+e,Log.SYSTEM);
			return false;
		} finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
		
		return true;
	}
	
	public String show() {
		checkId();
		StringBuffer sb=new StringBuffer();
		sb.append("<url>"+Server.getURL()+"</url>\n");
		sb.append("<timestamp>"+gTimestamp+"</timestamp>\n");
		sb.append("<key>"+gKey+"</key>\n");
		
		String template;
		int folder;
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the template exists and its permission
			ps=db.prepareStatement("SELECT " +
					"gallery.id, gallery.title, gallery.description, gallery.col, gallery.row, gallery.subfolder, gallery.style, gallery.last, gallery.count, gallery.sort_timestamp, gallery.sort_order, " +
					"gallery.key_folder AS folder_id, folder.name AS folder_name, " +
					"template.name AS template, " +
					"account.id AS user_id, account.name AS user_name, account.status AS user_status " +
					"FROM gallery, folder, template, account " +
					"WHERE gallery.timestamp=? AND gallery.key=? AND gallery.key_folder=folder.id AND gallery.key_template=template.id AND folder.key_user=account.id");
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			
			rs=ps.executeQuery();

			if(rs==null || !rs.next()) return "Invalid Gallery";
			
			template=rs.getString("template");
			folder=rs.getInt("folder_id");

			//build common XML
			gTemplate=rs.getString("template");
			gTitle=rs.getString("title");
			gDescription=rs.getString("description");
			gStyle=rs.getString("style");
			gFolder=rs.getString("folder_name");
			gCount=rs.getInt("count");
			gCol=rs.getShort("col");
			gRow=rs.getShort("row");
			gIsSubfolder=rs.getBoolean("subfolder");
			gSortBy=rs.getBoolean("sort_timestamp") ? "timestamp" : "name";
			gSortOrder=rs.getBoolean("sort_order") ? "DESC" : "ASC";
			
			String last=rs.getString("last");
			
			//get user information (id, name)
/*			ps=db.prepareStatement("SELECT account.id AS user_id, account.name AS user_name FROM account, folder WHERE folder.id=? AND folder.key_user=account.id LIMIT 1");
			ps.setInt(1, folder);
			rs=ps.executeQuery();

			if(rs==null || !rs.next()) return "Invalid User";
*/			

			gUser=rs.getString("user_name");
			int parentId=rs.getInt("user_id");
			int status=rs.getInt("user_status");
			
			sb.append("<template>"+gTemplate+"</template>\n");
			sb.append("<user status=\""+status+"\">"+gUser+"</user>\n");
			if(gTitle!=null) sb.append("<title>"+gTitle+"</title>\n");
			if(gDescription!=null) sb.append("<description>"+gDescription+"</description>\n");
			if(gStyle!=null) sb.append("<style>"+gStyle+"</style>\n");
			sb.append("<folder>"+gFolder+"</folder>\n");
			sb.append("<count>"+gCount+"</count>\n");
			sb.append("<last>"+last+"</last>\n");

			if(gCol!=-1) {
				sb.append("<col>"+gCol+"</col>\n");
				if(gRow!=-1) sb.append("<row>"+gRow+"</row>\n");
			}
			sb.append("<subfolder>"+gIsSubfolder+"</subfolder>\n");

			
			//update gallery (last, count);
			ps=db.prepareStatement("UPDATE gallery SET last=?, count=? WHERE id=?");
			ps.setString(1, DateTime.getTimestamp());
			ps.setInt(2, gCount+1);
			ps.setInt(3, rs.getInt("id"));
			
			ps.executeUpdate();
			
			//count images
			ps=db.prepareStatement("SELECT count(id) FROM file WHERE public=? AND status>=? AND (key_folder=?"+(gIsSubfolder ? SUBFOLDER : "")+")");
			ps.setBoolean(1, true);
			ps.setInt(2, DBFSFile.STATUS_DEFAULT);
			ps.setInt(3, folder);
			if(gIsSubfolder) {
				ps.setInt(4, parentId);
				ps.setString(5, gFolder);
			}

			rs=ps.executeQuery();
			
			if(rs==null || !rs.next()) throw new DatabaseException("Gallery.show: Invalid Result (folder-count)");

			gLast=rs.getInt("count");
			sb.append("<position>"+gPosition+"</position>\n");
			sb.append("<last>"+gLast+"</last>\n");
			
			
			//get images from folder
			if(gLast>0) {
				ps=db.prepareStatement("SELECT *, " +
						"(SELECT name FROM extension WHERE extension.id=file.key_extension) AS extension " +
						"FROM file " +
						"WHERE public=? AND status>=? AND (key_folder=?"+(gIsSubfolder ? SUBFOLDER : "")+") " +
						"ORDER BY "+gSortBy+", sort "+gSortOrder+" OFFSET ?"+((gCol!=-1 && gRow!=-1) ? " LIMIT ?" :""));
				ps.setBoolean(1, true);
				ps.setInt(2, DBFSFile.STATUS_DEFAULT);
				ps.setInt(3, folder);
				if(gIsSubfolder) {
					ps.setInt(4, parentId);
					ps.setString(5, gFolder);
					ps.setInt(6, gPosition-1);
					if(gCol!=-1 && gRow!=-1) ps.setInt(7, gCol*gRow);
				} else {
					ps.setInt(4, gPosition-1);
					if(gCol!=-1 && gRow!=-1) ps.setInt(5, gCol*gRow);
				}
				
				rs=ps.executeQuery();
				
				if(rs==null) throw new DatabaseException("Template.show: Invalid Result");

				while(rs.next()) {
					sb.append("<image>\n");
					sb.append("<name>"+rs.getString("name")+"</name>\n");
					sb.append("<timestamp>"+rs.getString("timestamp")+"</timestamp>\n");
					sb.append("<key>"+rs.getString("key")+"</key>\n");
					sb.append("<count>"+rs.getInt("count")+"</count>\n");
					if(rs.getString("description")!=null) sb.append("<description>"+rs.getString("description")+"</description>\n");
					sb.append("<adult>"+rs.getBoolean("adult")+"</adult>\n");
					sb.append("<width>"+rs.getInt("width")+"</width>\n");
					sb.append("<height>"+rs.getInt("height")+"</height>\n");
					sb.append("<size>"+rs.getInt("size")+"</size>\n");
					sb.append("<extension>"+rs.getString("extension")+"</extension>\n");
					sb.append("</image>\n");
				}
			}
			
		} catch(Exception e) {
			Log.write("Gallery.show: Database-Error\n"+e,Log.SYSTEM);
			return "Gallery.show: Database-Error\n"+e;
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
		
		try {
			//& --> &amp;
			return transform(template, "<gallery>\n"+Replace.replace(sb.toString(),"&","&amp;")+"</gallery>");
		} catch (XMLException e) {
			Log.write("Gallery.show: Unable to transform the gallery\n"+e, Log.SYSTEM);
			return "Gallery.show: Unable to transform the gallery\n"+e;
		}
	}

	/**
	 * Transforms the given XML-String with the given template into a output-string.
	 * 
	 * @param pTemplate
	 * @param pXML
	 * @return
	 * @throws XMLException
	 */
	public String transform(String pTemplate, String pXML) throws XMLException {
		try {
			pXML=Convert.UTF8ToLatin1(pXML);
		} catch(Exception e) {
			//do nothing
		}
		
	    //Init XML-File
	    XML xml=new XML(pXML);
	    xml.setResultType(XML.STRING);  //STRING is default;(DOM, STRING, FILE)
	    //XSL-File to convert XML
	    
	    xml.setOutputParameter(OutputKeys.METHOD,"xml");
	    xml.setOutputParameter(OutputKeys.ENCODING, "UTF-8");
	    xml.setOutputParameter(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    
	    xml.setXSLParameter("gallery", this);
	    xml.transform(new File(Lib.getPath()+File.separator+"template"+File.separator+pTemplate+File.separator+"template.xsl"));

	    return xml.getResultString();
	}

	public void setCSS(String pColor) {
		gCSS=pColor;
	}
	
	public String getCSS() {
		return gCSS;
	}
}
