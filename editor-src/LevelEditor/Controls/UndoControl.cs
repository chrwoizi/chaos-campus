using EditorObjects;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace Javy.Controls
{
    /// <summary>
    /// </summary>
    public partial class UndoControl : UserControl
    {
        private UndoManager manager;
        private bool undoManagerEditing = false;

        // für Versuchszwecke die History zu laden
        private Map mapOriginal;
        private UndoEventHandler undoEventHandler;


        /// <summary>
        /// </summary>
        public UndoControl()
        {
            InitializeComponent();
            manager = new UndoManager();
        }



        public UndoManager ActiveManager
        {
            get { return this.manager; }
            set 
            {
                this.manager = value;
                if (this.manager != null)
                {
                    undoManagerEditing = true;
                    manager.LoadToListView(listView1.Items);
                    undoManagerEditing = false;
                }
                else
                {
                    listView1.Items.Clear();
                }
            }
        }



        /// <summary>
        /// </summary>
        private void listView1_Resize(object sender, EventArgs e)
        {
            //if (listView1.ClientSize.Width > 0)
            //    listView1.TileSize = new Size(listView1.ClientSize.Width - 10,listView1.TileSize.Height);
        }



        private void UndoControl_Load(object sender, EventArgs e)
        {
            listView1.Items.Clear();
        }



        public void AddUndo(IUndo undo)
        {
            if (undo.GetType() == typeof(UndoMap))
            {
                this.mapOriginal = (undo as UndoMap).MapOriginal;
                this.undoEventHandler = (undo as UndoMap).UndoEventHandler;
            }
            undoManagerEditing = true;
            manager.Add(undo, listView1.Items);
            undoManagerEditing = false;
        }



        public void Undo()
        {
            if (listView1.SelectedIndices.Count > 0)
            {
                int selectedIndex = listView1.SelectedIndices[0];
                if (selectedIndex > 0)
                {
                    listView1.SelectedIndices.Clear();
                    listView1.SelectedIndices.Add(selectedIndex - 1);
                }
            }
        }



        public void Redo()
        {
            if (listView1.SelectedIndices.Count > 0)
            {
                int selectedIndex = listView1.SelectedIndices[0];
                if (selectedIndex + 1 < listView1.Items.Count)
                {
                    listView1.SelectedIndices.Clear();
                    listView1.SelectedIndices.Add(selectedIndex + 1);
                }
            }
        }



        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (undoManagerEditing)
                return;
            if (listView1.SelectedIndices.Count > 0)
            {
                if (listView1.Items.Count > 0)
                {
                    ListViewItem item = listView1.Items[listView1.SelectedIndices[0]];
                    UndoNode undoNode = null;
                    if (item != null)
                        undoNode = item.Tag as UndoNode;
                    if (undoNode != null)
                        manager.UndoRedoTo(undoNode, listView1.Items);
                }
            }
        }



        private void listView1_DrawItem(object sender, DrawListViewItemEventArgs e)
        {
            Color backColor = Color.Transparent;
            bool useItemStyleForSubItems = false;
            Font font = new Font(e.Item.Font, FontStyle.Regular);
            if (listView1.SelectedIndices.Count < 1)
                return;
            if (e.ItemIndex > listView1.SelectedIndices[0])
            {
                Color colorRed = Color.FromArgb(0, 255, 128, 128);
                backColor = colorRed;
                useItemStyleForSubItems = true;
                font = new Font(e.Item.Font, FontStyle.Regular);
            }
            if (e.ItemIndex < listView1.SelectedIndices[0])
            {
                backColor = Color.Silver;
                useItemStyleForSubItems = true;
                font = new Font(e.Item.Font, FontStyle.Regular);
            }
            if (e.ItemIndex == listView1.SelectedIndices[0])
            {
                backColor = Color.White;
                useItemStyleForSubItems = false;
                font = new Font(e.Item.Font, FontStyle.Bold);
            }
            if (!e.Item.Font.Equals(font))
                e.Item.Font = font;
            if (!e.Item.UseItemStyleForSubItems.Equals(useItemStyleForSubItems))
                e.Item.UseItemStyleForSubItems = useItemStyleForSubItems;
            if (!e.Item.BackColor.Equals(backColor))
                e.Item.BackColor = backColor;
            //e.DrawBackground();
            e.DrawDefault = true;
        }

        private void listView1_DrawColumnHeader(object sender, DrawListViewColumnHeaderEventArgs e)
        {
            e.DrawDefault = true;
        }

        private void listView1_DrawSubItem(object sender, DrawListViewSubItemEventArgs e)
        {
            e.DrawDefault = true;
        }

        private void listView1_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Alt && e.Control && e.Shift && e.KeyCode == Keys.F10)
            {
                ActiveManager.SaveToXmlFile("undo-test-save.xml");
            }
            if (e.Alt && e.Control && e.Shift && e.KeyCode == Keys.F11)
            {
                //undoManagerEditing = true;
                ActiveManager.ReadFromXmlFile("undo-test-save.xml", this.mapOriginal, this.undoEventHandler);
                ActiveManager.LoadToListView(listView1.Items);
                //undoManagerEditing = false;
            }
        }
    }
}
