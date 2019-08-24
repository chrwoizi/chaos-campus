using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Xml;
using EditorObjects;
using System.Drawing.Imaging;
using Javy.Controls;
using System.Net;
using Microsoft.VisualBasic;
using System.Threading;


namespace Javy
{
    public partial class TileControl : UserControl
    {
        public TileControl()
        {
            InitializeComponent();
        }

        // ToDo: private void SetBrushTile(int tileId,int tileSetId) ebenfalls überarbeitet, bitte überprüfen

        // ToDo: Nachträglich eingefügt; bitte Implementation überprüfen und korrigieren
        // Falls gerade ein MultiBrush (Selektion) aktiv ist soll dieser zurück gegeben werden, ansonsten Null
        public Map ActiveBrush
        {
            get
            {
                if (!isMultiSelection) return null;
                return Selection;
            }
        }

        /// <summary>
        /// Handles the Load event of the TileControl control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TileControl_Load(object sender, EventArgs e)
        {
            // enable doublebuffering
            SetStyle(ControlStyles.AllPaintingInWmPaint |
                          ControlStyles.UserPaint |
                          ControlStyles.DoubleBuffer, true);
            // set zero size of the tileset view
            TilesetDisplay.Width = 0;
            TilesetDisplay.Height = 0;
            // stretch combobox to fullfill resizing
            if (TilesetCombo != null) TilesetCombo.Width = Width -4;

            // add a MouseWheelHandler to the TilesetDisplay
            TilesetDisplay.MouseWheel += new MouseEventHandler(TilesetDisplay_MouseWheel);

            if (tilesetsConfigAutoLoad && !already_loaded)
            {
                // Get latest tileset configuration
                //DownloadTilesetsConfig();
                
                // Create default tileset
                string filePath = Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), "Tilesets\\DefaultTileset.png");
                if (!File.Exists(filePath))
                {
                    // create path if it does not exist:
                    Directory.CreateDirectory("Tilesets");
                    // create default tileset
                    Image defaultTileset = new Bitmap(16, 16, PixelFormat.Format24bppRgb);
                    Graphics g = Graphics.FromImage(defaultTileset);
                    g.Clear(Color.Black);
                    defaultTileset.Save(@"Tilesets\DefaultTileset.png", ImageFormat.Png);
                    defaultTileset.Dispose();
                }
                Add(new TilesetListItem(0, "DefaultTileset.png", "Default", "", ""));
                
                // Get latest tileset configuration
                //DownloadTilesetsConfig();
                //LoadFromXml(tilesetsConfigFile);
                UpdateTilesetReferences();

                if(File.Exists(TilesetsConfigFile))
                    LoadFromXml(TilesetsConfigFile);

                already_loaded = true;
            }
        }

        private bool already_loaded = false;

        /// <summary>
        /// Handles the Resize event of the TileControl control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TileControl_Resize(object sender, EventArgs e)
        {
            TilesetCombo.Width = this.Width -4;
        }

        private List<ITileset> tilesets = new List<ITileset>();

        // Refencer for Quick Access to List items
        public ITileset[] Tilesets = null; // Default: no tilesets are loaded!

        #region Properties

        private int selectedTile = -1;

        /// <summary>
        /// Specifies the selected tile id
        /// </summary>
        public int SelectedTile
        {
            get
            {
                return this.selectedTile;
            }
        }

        private int selectedTileset = -1;

        /// <summary>
        /// Gets the selected tileset.
        /// </summary>
        /// <value>The selected tileset.</value>
        public int SelectedTileset
        {
            get
            {
                return selectedTileset;
            }
        }

        private string tilesetsConfigFile;

        /// <summary>
        /// Gets or sets the tilesets config file.
        /// </summary>
        /// <value>Tilesets.config</value>
        public string TilesetsConfigFile
        {
            get
            {
                return tilesetsConfigFile;
            }
            set
            {
                tilesetsConfigFile = value;
            }
        }

        private bool tilesetsConfigAutoLoad;

        /// <summary>
        /// Gets or sets a value indicating whether tilesets config is auto loaded or not.
        /// </summary>
        /// <value>true</value>
        public bool TilesetsConfigAutoLoad
        {
            get
            {
                return tilesetsConfigAutoLoad;
            }
            set
            {
                tilesetsConfigAutoLoad = value;
            }
        }


        /// <summary>
        /// Gets the avaible tileset ids.
        /// </summary>
        /// <value>The avaible tileset ids.</value>
        public List<uint> AvaibleTilesetIds
        {
            get
            {
                List<uint> avaibleIds = new List<uint>();
                foreach (TilesetListItem item in TilesetList.Items)
                {
                    avaibleIds.Add(Convert.ToUInt32(item.index));
                }
                return avaibleIds;
            }
        }

        #endregion

        #region Tilesets Management

        #region Config Management

        /// <summary>
        /// Saves to XML.
        /// </summary>
        /// <param name="configFile">The config file.</param>
        public void SaveToXml(string configFile)
        {
            XmlTextWriter xmlOut = new XmlTextWriter(configFile, Encoding.UTF8);

            // Use indenting for readability
            xmlOut.Formatting = Formatting.Indented;
            xmlOut.WriteStartDocument();

            // Always begin file with identification and warning
            xmlOut.WriteComment("Javy Leveleditor Tileset Configuration File");
            xmlOut.WriteComment("This file stores all mapped tilesets for the editor.");

            // Associate a version number with the root element so that future version of the code
            // will be able to be backwards compatible or at least recognise out of date versions
            xmlOut.WriteStartElement("TilesetConfiguration");
            xmlOut.WriteAttributeString("FormatVersion", "1.1");

            // Contents
            xmlOut.WriteStartElement("TilesetsList");
            xmlOut.WriteAttributeString("Count", TilesetList.Items.Count.ToString());
            foreach (TilesetListItem item in TilesetList.Items)
            {
                if (item.index != 0)    // do not save default tileset
                {
                    xmlOut.WriteStartElement("Tileset");

                    xmlOut.WriteAttributeString("Index", item.index.ToString());
                    xmlOut.WriteAttributeString("Filename", item.Filename);
                    xmlOut.WriteAttributeString("Href", item.Href);
                    xmlOut.WriteAttributeString("Description", item.Description);
                    xmlOut.WriteAttributeString("Md5", item.Md5);

                    xmlOut.WriteEndElement();
                }
            }
            xmlOut.WriteEndElement();
            //xmlOut.WriteEndElement();

            /* Feature Request:
             * Quicktiles speichern:
             */
            xmlOut.WriteStartElement("QuickTiles");

            // ------- QuickTile 1: --------------
            xmlOut.WriteStartElement("QuickTile");
            xmlOut.WriteAttributeString("Button", "1");
            xmlOut.WriteAttributeString("TileId", QuickTile1.TileId.ToString());
            xmlOut.WriteAttributeString("TilesetId", QuickTile1.TilesetId.ToString());
            xmlOut.WriteEndElement();
            // ------- QuickTile 1 End -----------

            // ------- QuickTile 2: --------------
            xmlOut.WriteStartElement("QuickTile");
            xmlOut.WriteAttributeString("Button", "2");
            xmlOut.WriteAttributeString("TileId", QuickTile2.TileId.ToString());
            xmlOut.WriteAttributeString("TilesetId", QuickTile2.TilesetId.ToString());
            xmlOut.WriteEndElement();
            // ------- QuickTile 2 End -----------

            // ------- QuickTile 3: --------------
            xmlOut.WriteStartElement("QuickTile");
            xmlOut.WriteAttributeString("Button", "3");
            xmlOut.WriteAttributeString("TileId", QuickTile3.TileId.ToString());
            xmlOut.WriteAttributeString("TilesetId", QuickTile3.TilesetId.ToString());
            xmlOut.WriteEndElement();
            // ------- QuickTile 3 End -----------

            // ------- QuickTile 4: --------------
            xmlOut.WriteStartElement("QuickTile");
            xmlOut.WriteAttributeString("Button", "4");
            xmlOut.WriteAttributeString("TileId", QuickTile4.TileId.ToString());
            xmlOut.WriteAttributeString("TilesetId", QuickTile4.TilesetId.ToString());
            xmlOut.WriteEndElement();
            // ------- QuickTile 4 End -----------

            xmlOut.WriteEndElement();
            xmlOut.WriteEndDocument();
            xmlOut.Flush();
            xmlOut.Close();
        }

        /// <summary>
        /// Loads from XML.
        /// </summary>
        /// <param name="configFile">The config file.</param>
        public void LoadFromXml(string configFile)
        {
            if(!File.Exists(configFile))
            {
                throw new Exception("Error: Tileset XML Configuration File does not exist.");
            }
            
            XmlTextReader xmlIn = new XmlTextReader(configFile);
            xmlIn.WhitespaceHandling = WhitespaceHandling.None;
            xmlIn.MoveToContent();

            while (!xmlIn.Name.Equals("TilesetConfiguration"))
            {
                if (!MoveToNextElement(xmlIn))
                    throw new ArgumentException("Invalid Tileset Configuration File!");
            }

            // Verify correct version:
            bool loadQuickTiles = true;
            string formatVersion = xmlIn.GetAttribute("FormatVersion");
            if (formatVersion != "1.1")
            {
                // Added Version 1.1 with QuickTile support
                if(formatVersion == "1.0")
                    loadQuickTiles = false;
                else
                    throw new ArgumentException("Unsupported FormatVersion while loading tileset configuration xml file!");
            }
            
            // Load Tilesets
            MoveToNextElement(xmlIn);
            if (xmlIn.Name != "TilesetsList")
                throw new ArgumentException("Error while loading tileset configuration xml file!");
            int countOfTilesets = Convert.ToInt32(xmlIn.GetAttribute("Count"));

            // create a temporary array to order tiles
            //string[,] tilesets = new string[countOfTilesets,4];

            List<TilesetListItem> tilesets= new List<TilesetListItem>();

            // to the tileset elements
            MoveToNextElement(xmlIn);
            while (xmlIn.Name == "Tileset")
            {
                // add each tileset to the tilesets-list
                tilesets.Add( new TilesetListItem(
                    Int32.Parse(xmlIn.GetAttribute("Index")),
                    xmlIn.GetAttribute("Filename"),
                    xmlIn.GetAttribute("Description"),
                    xmlIn.GetAttribute("Href"),
                    xmlIn.GetAttribute("Md5")));
                MoveToNextElement(xmlIn);
            }
            
            // Add Tilesets to Control
            AddTilesets(tilesets);

            // Load QuickTiles
            if (xmlIn.Name == "QuickTiles" && loadQuickTiles)
            {
                MoveToNextElement(xmlIn);


                while (xmlIn.Name == "QuickTile")
                {
                    // add each QuickTile
                    switch (Int32.Parse(xmlIn.GetAttribute("Button")))
                    {
                        case 1:
                            SetBrushTile(Int32.Parse(xmlIn.GetAttribute("TileId")), Int32.Parse(xmlIn.GetAttribute("TilesetId")), TileBrush1, 0);
                            break;
                        case 2:
                            SetBrushTile(Int32.Parse(xmlIn.GetAttribute("TileId")), Int32.Parse(xmlIn.GetAttribute("TilesetId")), TileBrush2, 1);
                            break;
                        case 3:
                            SetBrushTile(Int32.Parse(xmlIn.GetAttribute("TileId")), Int32.Parse(xmlIn.GetAttribute("TilesetId")), TileBrush3, 2);
                            break;
                        case 4:
                            SetBrushTile(Int32.Parse(xmlIn.GetAttribute("TileId")), Int32.Parse(xmlIn.GetAttribute("TilesetId")), TileBrush4, 3);
                            break;

                        default:
                            MessageBox.Show("Error while Loading Tilesets-Configfile. Problem while loading QuickTiles");
                            break;
                    }
                    MoveToNextElement(xmlIn);
                }
            }
            xmlIn.Close();

        }

        /// <summary>
        /// Moves to next element.
        /// </summary>
        /// <param name="xmlIn">The XML in.</param>
        /// <returns></returns>
        private static bool MoveToNextElement(XmlTextReader xmlIn)
        {
            if (!xmlIn.Read())
                return false;

            while (xmlIn.NodeType == XmlNodeType.EndElement)
            {
                if (!xmlIn.Read())
                    return false;
            }

            return true;
        }

        /// <summary>
        /// Handles the Click event of the saveTilesetConfig control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void saveTilesetConfig_Click(object sender, EventArgs e)
        {
            SaveFileDialog SaveDialog = new SaveFileDialog();
            SaveDialog.Filter = "Tilsets Config|*.config|All files|*.*";
            SaveDialog.Title = "Save Tileset Config...";

            if (SaveDialog.ShowDialog(ParentForm) == DialogResult.OK)
            {
                SaveToXml(SaveDialog.FileName);
            }
        }

        /// <summary>
        /// Handles the Click event of the openTilesetToolStripButton control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void openTilesetToolStripButton_Click(object sender, EventArgs e)
        {
            OpenFileDialog OpenDialog = new OpenFileDialog();
            OpenDialog.Filter = "Tilsets Config|*.config|All files|*.*";
            OpenDialog.Title = "Open Tileset Config...";

            if (OpenDialog.ShowDialog(ParentForm) == DialogResult.OK)
            {
                ClearLists();
                LoadFromXml(OpenDialog.FileName);
            }
        }

        private void ClearLists()
        {
            List<TilesetListItem> items = new List<TilesetListItem>();
            foreach (TilesetListItem item in TilesetList.Items)
            {
                if(item.index != 0)
                {
                    items.Add(item);
                }
            }
            foreach (TilesetListItem item in items)
            {
                    TilesetList.Items.Remove(item);
                    TilesetCombo.Items.Remove(item);
            }
        }

        #endregion

        /// <summary>
        /// Adds the tilesets.
        /// </summary>
        /// <param name="tileset">The tileset.</param>
        private void AddTilesets(List<TilesetListItem> tileset)
        {
            foreach (TilesetListItem item in tileset)
            {
                AddTileset(item);
            }
            UpdateTilesetReferences();
        }

        private void UpdateTilesetReferences()
        {
            // assign default length
            if (Tilesets == null)
                Tilesets = new ITileset[1];

            int largest = Tilesets.Length;
            foreach (TilesetListItem item in TilesetList.Items)
            {
                // Determine largest index
                if(largest < item.index)
                    largest = item.index;
            }
            
            // Nothing Changed, or the new tileset is already
            // in the Tilesets-Array scope
            if(largest == Tilesets.Length-1)
                return;
            
            // create an updated array with new size
            ITileset[] tiles = new ITileset[largest +1];

            //map virtual index to program internal indexes

            foreach (TilesetListItem item in TilesetList.Items)
            {
                // check wether the tileset is not loaded yet
                if (Tilesets.Length <= item.index || Tilesets[item.index] == null)
                {
                    // load the missing tileset:
                    tilesets.Add(new PNGTileset(@"Tilesets\" + item.Filename));
                    // and assign it to the reference-array:
                    tiles[item.index] = tilesets[tilesets.Count - 1];

                }
                    // Todo: maybe problem when reassigning tilesets to an already assigned id

                else
                {
                    // otherwise reassign the old reference to the new array:
                    tiles[item.index] = Tilesets[item.index];
                }
            }
            
            // now update the reference array:
            Tilesets = tiles;
        }


        private WebClient HttpClient = new WebClient();


        /// <summary>
        /// Adds the tile set.
        /// </summary>
        /// <param name="item">The item.</param>
        public void Add(TilesetListItem item)
        {
            if(!Directory.Exists("Tilesets"))
            {
                // create Tilesets Directory, if it does not exist.
                Directory.CreateDirectory("Tilesets");
            }

            // examine wether the tileset is already present
            if (!File.Exists(@"Tilesets\" + item.Filename))
            {
                // if not, dynamically download them from the internet
                try
                {
                    if(item.Href == "" || item.Href == null)
                        return;
                    HttpClient.DownloadFile(item.Href, @"Tilesets\" + item.Filename);
                    
                }
                catch( WebException ex)
                {
                    MessageBox.Show("Fehler beim Downloaden des Tilesets: " + item.Href + ", Error: " + ex.Message );
                }
            }

            //add the tilesets now:
            TilesetList.Items.Add(item);
            TilesetCombo.Items.Add(item);
            UpdateTilesetReferences();
        }

        /// <summary>
        /// Adds the tileset fast without a call to UpdateTilesetReferences()
        /// which loads the tileset
        /// </summary>
        /// <param name="item">The item.</param>
        private void AddTileset(TilesetListItem item)
        {
            if (!Directory.Exists("Tilesets"))
            {
                // create Tilesets Directory, if it does not exist.
                Directory.CreateDirectory("Tilesets");
            }

            // examine wether the tileset is already present
            if (!File.Exists(@"Tilesets\" + item.Filename))
            {
                // if not, dynamically download them from the internet
                try
                {
                    if (item.Href == "" || item.Href == null)
                        return;
                    HttpClient.DownloadFile(item.Href, @"Tilesets\" + item.Filename);

                }
                catch (WebException ex)
                {
                    MessageBox.Show("Fehler beim Downloaden des Tilesets: " + item.Href + ", Error: " + ex.Message);
                }
            }

            //add the tilesets now:
            TilesetList.Items.Add(item);
            TilesetCombo.Items.Add(item);
        }


        public void Remove(int index)
        {
            TilesetListItem foundItem = new TilesetListItem(0,"","","","");
            // Gehe durch alle Items
            foreach (TilesetListItem item in TilesetList.Items)
            {
                if(item.index == index)
                    foundItem = item;

            }
            
            if (foundItem.index == 0 &&
                foundItem.Filename == "" &&
                foundItem.Description == "" &&
                foundItem.Md5 == "")
            {   // if item found
                TilesetList.Items.Remove(foundItem);
                TilesetCombo.Items.Remove(foundItem);
            }
        }

        /// <summary>
        /// Removes the tile set.
        /// </summary>
        /// <param name="listId">The tileset id.</param>
        private void RemoveTileset(int listId, int delIndex)
        {
            if (listId < 1 || listId > Tilesets.Length)
                return;
            if (Tilesets[delIndex] != null)
            {
                // This part needs hardly to be fixed!
                //Tilesets.RemoveAt(listId);
                //TilesetDisplay.Width = 0;
                //TilesetDisplay.Height = 0;
                foreach (TilesetListItem tileset in TilesetList.Items)
                {
                    if (Tilesets[tileset.index] != null)
                    {
                        TilesetDisplay.Image = ((PNGTileset) Tilesets[tileset.index]).Tileset;
                    }
                }
                if(TilesetList.Items.Count <= listId)
                {
                    return;
                }
                //int delIndex = ((TilesetListItem)TilesetList.Items[listId]).index;
                TilesetList.Items.RemoveAt(listId);
                TilesetCombo.Items.RemoveAt(listId);
                Tilesets[delIndex] = null;
            }
        }

        /// <summary>
        /// Handles the SelectedIndexChanged event of the TilesetList control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TilesetList_SelectedIndexChanged(object sender, EventArgs e)
        {
            // set the actual selected Tileset
            if (TilesetList.SelectedIndex != -1)
            {
                selectedTileset = ((TilesetListItem) TilesetList.SelectedItem).index;
                visibleTileset = ((TilesetListItem)TilesetList.SelectedItem).index;
            }
            else
            {
                selectedTileset = 0;
                visibleTileset = 0;
            }
            TilesetDisplay.Image = ((PNGTileset)Tilesets[selectedTileset]).Tileset;
        }

        /// <summary>
        /// Handles the SelectedIndexChanged event of the TilesetCombo control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TilesetCombo_SelectedIndexChanged(object sender, EventArgs e)
        {
            // set the actual selected Tileset
            // set the actual selected Tileset
            if (TilesetCombo.SelectedIndex != -1)
            {
                selectedTileset = ((TilesetListItem)TilesetCombo.SelectedItem).index;
                visibleTileset = ((TilesetListItem)TilesetCombo.SelectedItem).index;
            }
            else
            {
                selectedTileset = 0;
                visibleTileset = 0;
            }
            TilesetDisplay.Image = ((PNGTileset)Tilesets[selectedTileset]).Tileset;
            
        }

        private void addTilesetToolStripButton_Click(object sender, EventArgs e)
        {
            //openTilesetDialog.ShowDialog(ParentForm);
            if (openTilesetDialog.ShowDialog(ParentForm) == DialogResult.OK)
            {
                if (File.Exists(openTilesetDialog.FileName))
                {
                    string inputId = Interaction.InputBox("What Tileset-ID should be assigned to the selected tileset?",
                                         "Assign Tileset-ID", Tilesets.Length.ToString(), 200, 200);

                    int id;
                    if(Int32.TryParse(inputId,out id))
                    {
                        string inputDesc =
                            Interaction.InputBox("Which description should the selected tileset have?", "Assign Description",
                                                 "Tileset" + inputId, 200, 200);   
                        if(inputDesc != "" || inputDesc != null)
                        {
                            try
                            {
                                if (!File.Exists(Path.GetDirectoryName(Application.ExecutablePath) +
                                              @"\Tilesets\" + Path.GetFileName(openTilesetDialog.FileName)))
                                {
                                    File.Copy(openTilesetDialog.FileName,Path.GetDirectoryName(Application.ExecutablePath)+
                                              @"\Tilesets\" + Path.GetFileName(openTilesetDialog.FileName), true);
                                }
                            }
                            catch(Exception ex)
                            {
                                MessageBox.Show(ex.Message,"Error while Loading!");
                                return;
                            }

                            TilesetListItem item = new TilesetListItem(id, Path.GetFileName(openTilesetDialog.FileName), inputDesc, "", "");
                            Add(item);
                            UpdateTilesetReferences();
                        }
                    }
                    else
                    {
                        MessageBox.Show("Error: Incorrect ID!");
                    }
                }
            }
        }

        private void removeTilesetToolStripButton_Click(object sender, EventArgs e)
        {
            if ((TilesetList.SelectedIndex >= 0)
                && (TilesetList.SelectedIndex < TilesetList.Items.Count))
                RemoveTileset(TilesetList.SelectedIndex, ((TilesetListItem)TilesetList.SelectedItem).index);
        }

        #endregion

        #region TilesetDisplay

        private int overTileX = 0;
        private int overTileY = 0;
        private int overTile = 0;
        private int selectedTileX = -1;
        private int selectedTileY = -1;

        private bool mouseInside = false;

        private bool isSelectionActive = false;

        /// <summary>
        /// Handles the Paint event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.Windows.Forms.PaintEventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            DrawTargetSelector(g);
            DrawSelectedTile(g);
            DrawMultiSelection(g);
        }

        /// <summary>
        /// Draws the selected tile.
        /// </summary>
        /// <param name="g">The g.</param>
        private void DrawSelectedTile(Graphics g)
        {
            if (isMultiSelection)
                return;
            Pen pen = new Pen(Color.GreenYellow);

            g.DrawRectangle(pen,
                selectedTileX * tileSize - borderSize,
                selectedTileY * tileSize - borderSize,
                tileSize + borderSize,
                tileSize + borderSize);
        }

        /// <summary>
        /// Draws the target selector.
        /// </summary>
        /// <param name="g">The g.</param>
        private void DrawTargetSelector(Graphics g)
        {
            Pen pen = new Pen(Color.WhiteSmoke);
            g.DrawLine(pen, overTileX * tileSize - borderSize, 0, overTileX * tileSize - borderSize, TilesetDisplay.Height);
            g.DrawLine(pen, overTileX * tileSize + tileSize, 0, overTileX * tileSize + tileSize, TilesetDisplay.Height);

            g.DrawLine(pen, 0, overTileY * tileSize - borderSize, TilesetDisplay.Width, overTileY * tileSize - borderSize);
            g.DrawLine(pen, 0, overTileY * tileSize + tileSize, TilesetDisplay.Width, overTileY * tileSize + tileSize);
        }

        private int borderSize = 1;

        private int tileSize = 16;


        /// <summary>
        /// Draws the multi selection.
        /// </summary>
        /// <param name="g">The g.</param>
        private void DrawMultiSelection(Graphics g)
        {
            if (isMultiSelection)
            {
                Pen pen = new Pen(Color.GreenYellow);
                Rectangle rect = new Rectangle();

                // verify coordinates are in correct range:

                if (multiStartX > multiEndX)
                {
                    rect.X = multiEndX * tileSize - borderSize;
                    rect.Width = (multiStartX * tileSize) - (multiEndX * tileSize) + tileSize + borderSize;
                }
                else
                {
                    rect.X = multiStartX * tileSize - borderSize;
                    rect.Width = (multiEndX * tileSize) - (multiStartX * tileSize) + tileSize + borderSize;
                }

                if (multiStartY > multiEndY)
                {
                    rect.Y = multiEndY * tileSize - borderSize;
                    rect.Height = (multiStartY * tileSize) - (multiEndY * tileSize) + tileSize + borderSize;
                }
                else
                {
                    rect.Y = multiStartY * tileSize - borderSize;
                    rect.Height = (multiEndY * tileSize) - (multiStartY * tileSize) + tileSize + borderSize;
                }

                // draw multi selection rect
                g.DrawRectangle(pen,rect);
            }
        }


        /// <summary>
        /// Horizontals the lines.
        /// </summary>
        /// <param name="g">The g.</param>
        private void HorizontalLines(Graphics g)
        {
            Pen pen = new Pen(Color.Blue);
            int length = TilesetDisplay.Height / 16;

            for (int i = 0; i < length; i++)
            {
                g.DrawLine(pen, 0, i*16, TilesetDisplay.Width,i*16);
            }

        }

        /// <summary>
        /// Verticals the lines.
        /// </summary>
        /// <param name="g">The g.</param>
        private void VerticalLines(Graphics g)
        {
            Pen pen = new Pen(Color.Blue);
            int length = TilesetDisplay.Width/16;

            for (int i = 0; i < length; i++)
            {
                g.DrawLine(pen, i*16, 0, i*16, TilesetDisplay.Height);
            }
            
        }

        /// <summary>
        /// Handles the MouseEnter event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_MouseEnter(object sender, EventArgs e)
        {
            //EditorMDI.StatusLabel.Text = "Select a tile with your left mousebutton or copy a multiple selection of tiles with your right mousebutton.";
            mouseInside= true;
        }

        /// <summary>
        /// Handles the MouseLeave event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_MouseLeave(object sender, EventArgs e)
        {
            //EditorMDI.StatusLabel.Text = "";
            mouseInside = false;
        }

        /// <summary>
        /// Handles the MouseMove event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.Windows.Forms.MouseEventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_MouseMove(object sender, MouseEventArgs e)
        {
          
            if (TilesetDisplay.Image == null)
                return;

            if (mouseInside)
            {
                overTileX = e.X/16;
                overTileY = e.Y/16;
                overTile = overTileY * (TilesetDisplay.Image.Size.Width / 16) + overTileX;
                UpdateTileInfo();
                TilesetDisplay.Refresh();
            }
            else
            {
                overTileX = -1;
                overTileY = -1;
            }
            if (isSelectionActive)
            {
                //HandleSelection();
                HandleMultiSelection();
            }
        }

        /// <summary>
        /// Handles the Click event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_Click(object sender, EventArgs e)
        {
            HandleSelection();
        }

        // Helper for Multi-Selection
        private int multiStartX = -1;
        private int multiStartY = -1;
        private int multiEndX = -1;
        private int multiEndY = -1;
        private bool isMultiSelection = false;

        /// <summary>
        /// Handles the MouseDown event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.Windows.Forms.MouseEventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                ClipboardManager.SetData(Selection);
            }
            else 
            {
                isSelectionActive = true;

                // Save starting point for selection
                multiStartX = overTileX;
                multiStartY = overTileY;
                // Reset the selection
                multiEndX = multiStartX;
                multiEndY = multiStartY;
            }
        }

        /// <summary>
        /// Handles the MouseUp event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.Windows.Forms.MouseEventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_MouseUp(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                isSelectionActive = false;
                HandleMultiSelection();
            }
        }
        /// <summary>
        /// Handles the MouseWheel event of the TilesetDisplay control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.Windows.Forms.MouseEventArgs"/> instance containing the event data.</param>
        private void TilesetDisplay_MouseWheel(object sender, MouseEventArgs e)
        {
            MessageBox.Show("TEST!" + e.Delta.ToString());
        }

        /// <summary>
        /// Handles the selection.
        /// </summary>
        private void HandleSelection()
        {
            if (TilesetDisplay.Image != null && isSelectionActive)
            {
                selectedTileX = overTileX;
                selectedTileY = overTileY;
                selectedTile = overTile;
                SetBrushTile(selectedTile, visibleTileset);
            }
        }

        /// <summary>
        /// Handles the multi selection.
        /// </summary>
        private void HandleMultiSelection()
        {

            if ((multiStartX != -1 && multiStartY != -1) &&
                (multiStartX != overTileX || multiStartY != overTileY))
            {
                multiEndX = overTileX;
                multiEndY = overTileY;

                // is there anything selected?
                if (multiStartX != multiEndX || multiStartY != multiEndY)
                {
                    isMultiSelection = true;

                    if (!isSelectionActive)
                    {
                        // save selected tiles
                        Selection = SaveSelection();
                    }
                }
                else
                {
                    // reset to defaults (no selection)
                    multiStartX = -1;
                    multiStartY = -1;
                    multiEndX = -1;
                    multiEndY = -1;
                    // reset selection map too:
                    Selection = null;

                    isMultiSelection = false;

                    // now handle the simple selection
                    HandleSelection();
                }
            }
        }

        private Map Selection = null;

        /// <summary>
        /// Saves the mutli-selection.
        /// </summary>
        /// <returns>Map</returns>
        private Map SaveSelection()
        {

            // fresh vars
            int helpX = 0, 
                helpY = 0, 
                startX = multiStartX,
                startY = multiStartY,
                endX = multiEndX,
                endY = multiEndY;

            // to correct range:
            
            if(startX > endX)
            {
                helpX = startX;
                startX = endX;
                endX = helpX;
            }
            if (startY > endY)
            {
                helpY = startY;
                startY = endY;
                endY = helpY;
            }
            int width = endX - startX +1;
            int height = endY - startY +1;

            // create new selection-map to store selected tiles
            Map map = new Map(width, height);

            // save each tile to the selection-map
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    // calculate the tileId
                    int tileId = (startY + y) * 
                        (TilesetDisplay.Image.Size.Width / tileSize) + (startX + x);

                    map.SetField(x, y, new InternalField((uint)
                        ((tileId & 0xFFFF) | (SelectedTileset << 16))));
                }
            }

            return map;
        }

        /// <summary>
        /// Updates the tile info.
        /// </summary>
        private void UpdateTileInfo()
        {
            TileInfo.Text = "ID: " + overTile;
            TileInfo.Image = new Bitmap(16, 16);
            Graphics g = Graphics.FromImage(TileInfo.Image);
            if(selectedTile != -1)
                Tilesets[selectedTileset].DrawTile(overTile, 0, 0, g);
            toolStrip2.Refresh();
        }

        #endregion

        #region TileBrush 1-4

        // ID des aktiven Quicktiles
        public int QuickTile
        {
            set
            {
                // toggle buttons
                TileBrush1.Checked = value == 1;
                TileBrush2.Checked = value == 2;
                TileBrush3.Checked = value == 3;
                TileBrush4.Checked = value == 4;
                // set brushdata to control
                selectedTile = brushData[value - 1, 0];
                if (brushData[value - 1, 1] != -1)
                    selectedTileset = brushData[value - 1, 1];
            }
        }
        /// <summary>
        /// Handles the Click event of the TileBrushX control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TileBrush_Click(object sender, EventArgs e)
        {
            int id = 0;
            if (sender == TileBrush1) id = 1;
            if (sender == TileBrush2) id = 2;
            if (sender == TileBrush3) id = 3;
            if (sender == TileBrush4) id = 4;
            QuickTile = id;
        }

        private int[,] brushData = new int[4, 2] { { 0, -1 }, { 0, -1 }, { 0, -1 },{ 0, -1} };
        private int visibleTileset = -1;

        /// <summary>
        /// Sets the brush tile.
        /// </summary>
        /// <param name="tileId">The tile id.</param>
        /// <param name="tilesetId">The tileset id.</param>
        private void SetBrushTile(int tileId, int tilesetId)
        {
            isMultiSelection = false;

            ToolStripButton sButton = null;
            int sId = -1;
            if (TileBrush1.Checked) { sButton = TileBrush1; sId = 0; }
            if (TileBrush2.Checked) { sButton = TileBrush2; sId = 1; }
            if (TileBrush3.Checked) { sButton = TileBrush3; sId = 2; }
            if (TileBrush4.Checked) { sButton = TileBrush4; sId = 3; }
            if (sId < 0) return;

            SetBrushTile(tileId,tilesetId,sButton, sId);
        }


        /// <summary>
        /// Sets the brush tile.
        /// </summary>
        /// <param name="tileId">The tile id.</param>
        /// <param name="tileSetId">The tile set id.</param>
        /// <param name="sButton">The BrushButton.</param>
        private void SetBrushTile(int tileId, int tileSetId, ToolStripButton sButton, int sId)
        {
            // update button icon for TileBrush1
            sButton.Image = Tilesets[tileSetId].GetTile(tileId);
            // save brush data to a simple array
            // [x,0] = tileId
            // [x,1] = tilesetId
            brushData[sId, 0] = tileId;
            brushData[sId, 1] = tileSetId;

            // set ToolTip-Information for this brush:
            sButton.ToolTipText = string.Format(
                "Brush {0} - Tile: {1} Tileset: {2}",
                sId+1, tileId, tileSetId);

            /*
            if(TileBrush1.Checked)
            {
                // update button icon for TileBrush1
                this.TileBrush1.Image = new Bitmap(16, 16);
                // get graphics context for the button icon
                Graphics g = Graphics.FromImage(this.TileBrush1.Image);
                // set the actual selected tile as icon
                Tilesets[tileSetId].DrawTile(tileId, 0, 0, g);
                // save brush data to a simple array
                // [x,0] = tileId
                // [x,1] = tilesetId
                brushData[0, 0] = tileId;
                brushData[0, 1] = tileSetId;

                // set ToolTip-Information for this brush:
                TileBrush1.ToolTipText = string.Format(
                    "Brush 1 - Tile: {0} Tileset: {1}",
                    tileId, tileSetId);
            }
            else if(TileBrush2.Checked)
            {
                // update button icon for TileBrush2
                this.TileBrush2.Image = new Bitmap(16, 16);
                // get graphics context for the button icon
                Graphics g = Graphics.FromImage(this.TileBrush2.Image);
                // set the actual selected tile as icon
                Tilesets[tileSetId].DrawTile(tileId, 0, 0, g);
                // save brush data to a simple array
                // [x,0] = tileId
                // [x,1] = tilesetId
                brushData[1, 0] = tileId;
                brushData[1, 1] = tileSetId;
                // set ToolTip-Information for this brush:
                TileBrush2.ToolTipText = string.Format(
                    "Brush 2 - Tile: {0} Tileset: {1}",
                    tileId, tileSetId);
            }
            else if (TileBrush3.Checked)
            {
                // update button icon for TileBrush3
                this.TileBrush3.Image = new Bitmap(16, 16);
                // get graphics context for the button icon
                Graphics g = Graphics.FromImage(this.TileBrush3.Image);
                // set the actual selected tile as icon
                Tilesets[tileSetId].DrawTile(tileId, 0, 0, g);
                // save brush data to a simple array
                // [x,0] = tileId
                // [x,1] = tilesetId
                brushData[2, 0] = tileId;
                brushData[2, 1] = tileSetId;
                // set ToolTip-Information for this brush:
                TileBrush3.ToolTipText = string.Format(
                    "Brush 3 - Tile: {0} Tileset: {1}",
                    tileId, tileSetId);
            }
            else if (TileBrush4.Checked)
            {
                // update button icon for TileBrush4
                this.TileBrush4.Image = new Bitmap(16, 16);
                // get graphics context for the button icon
                Graphics g = Graphics.FromImage(this.TileBrush4.Image);
                // set the actual selected tile as icon
                Tilesets[tileSetId].DrawTile(tileId, 0, 0, g);
                // save brush data to a simple array
                // [x,0] = tileId
                // [x,1] = tilesetId
                brushData[3, 0] = tileId;
                brushData[3, 1] = tileSetId;
                // set ToolTip-Information for this brush:
                TileBrush4.ToolTipText = string.Format(
                    "Brush 4 - Tile: {0} Tileset: {1}",
                    tileId, tileSetId);
            }
            */
        }
        #endregion


        /// <summary>
        /// Handles the MouseMove event of the TileControl control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.Windows.Forms.MouseEventArgs"/> instance containing the event data.</param>
        private void TileControl_MouseMove(object sender, MouseEventArgs e)
        {
            TilesetCombo.Focus();
        }

        /// <summary>
        /// Gets the quick tile1.
        /// </summary>
        /// <value>The quick tile1.</value>
        public QuickTile QuickTile1
        {
            get
            {
                return new QuickTile(brushData[0,0],brushData[0,1]);
            }
            set
            {
                brushData[0, 0] = value.TileId;
                brushData[0, 1] = value.TilesetId;
            }
        }

        /// <summary>
        /// Gets the quick tile2.
        /// </summary>
        /// <value>The quick tile2.</value>
        public QuickTile QuickTile2
        {
            get
            {
                return new QuickTile(brushData[1, 0], brushData[1, 1]);
            }
            set
            {
                brushData[1, 0] = value.TileId;
                brushData[1, 1] = value.TilesetId;
            }
        }

        /// <summary>
        /// Gets the quick tile3.
        /// </summary>
        /// <value>The quick tile3.</value>
        public QuickTile QuickTile3
        {
            get
            {
                return new QuickTile(brushData[2, 0], brushData[2, 1]);
            }
            set
            {
                brushData[2, 0] = value.TileId;
                brushData[2, 1] = value.TilesetId;
            }
        }

        /// <summary>
        /// Gets the quick tile4.
        /// </summary>
        /// <value>The quick tile4.</value>
        public QuickTile QuickTile4
        {
            get
            {
                return new QuickTile(brushData[3, 0], brushData[3, 1]);
            }
            set
            {
                brushData[3, 0] = value.TileId;
                brushData[3, 1] = value.TilesetId;
            }
        }

        private void syncTilesetsToolStripButton_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Possibly added Tilesets will be altered, are you sure", "Checkout Tilesets Config", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                // Get latest config:
                DownloadWorkerThread.RunWorkerAsync();
            }
        }

        /// <summary>
        /// Downloads the latest tileset configuration from the internet.
        /// </summary>
        private void DownloadTilesetsConfig()
        {
            // Download Tileset Config
            HttpClient.DownloadFile(Javy.Properties.Settings.Default["TilesetsUrl"].ToString(), tilesetsConfigFile);
        }

        private void copySelectionToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // Save Map to Clipboard
            ClipboardManager.SetData(Selection);
        }

        private void DownloadWorkerThread_DoWork(object sender, DoWorkEventArgs e)
        {
            DownloadTilesetsConfig();
        }

        /// <summary>
        /// Handles the RunWorkerCompleted event of the DownloadWorkerThread control.
        /// This Event raises, when the download succeeded
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.ComponentModel.RunWorkerCompletedEventArgs"/> instance containing the event data.</param>
        private void DownloadWorkerThread_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            // Clear List
            ClearLists();
            // load latest config:
            LoadFromXml(tilesetsConfigFile);
        }

        /// <summary>
        /// Determines whether the specified tileset-id is loaded or not.
        /// </summary>
        /// <param name="tilesetId">The tileset id.</param>
        /// <returns>
        /// 	<c>true</c> if the specified tileset id is loaded; otherwise, <c>false</c>.
        /// </returns>
        public bool isLoaded(int tilesetId)
        {
            if(Tilesets.Length > tilesetId)
            {
                if(Tilesets[tilesetId] != null)
                {
                    return true;
                }
            }
            return false;
        }


    

    }

    public struct QuickTile
    {
        public int TileId;
        public int TilesetId;

        /// <summary>
        /// Initializes a new instance of the <see cref="QuickTile"/> class.
        /// </summary>
        /// <param name="tileId">The tile id.</param>
        /// <param name="tilesetId">The tileset id.</param>
        public QuickTile( int tileId, int tilesetId)
        {
            if (tilesetId < 0)
                TileId = 0;
            else
                TileId = tileId;
            if (tilesetId < 0)
                TilesetId = 0;
            else
                TilesetId = tilesetId;
        }

        /// <summary>
        /// Toes the graphic id.
        /// </summary>
        /// <returns></returns>
        public uint ToGraphicId()
        {
            return (uint)((TilesetId << 16) | (TileId & 0xFFFF));
        }
    }
    /// <summary>
    /// A TilesetListItem for better Loading and Tileset Handling
    /// </summary>
    public struct TilesetListItem
    {
        public int index;
        public string Filename;
        public string Description;
        public string Href;
        public string Md5;
        /// <summary>
        /// Initializes a new instance of the <see cref="TilesetListItem"/> class.
        /// </summary>
        /// <param name="index">The index.</param>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="description">The description.</param>
        /// <param name="href">The href.</param>
        /// <param name="md5">The MD5.</param>
        public TilesetListItem(int index, string fileName, string description, string href, string md5)
        {
            this.index = index;
            Filename = fileName;
            Description = description;
            Href = href;
            Md5 = md5;
        }
        public override string ToString()
        {
            return index + ": " + Description;
        }
    }

}
