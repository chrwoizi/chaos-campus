using System.IO;
using System.Windows.Forms;
namespace Javy
{
    partial class TilesetToolForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tileControl1 = new Javy.TileControl();
            this.SuspendLayout();
            // 
            // tileControl1
            // 
            this.tileControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tileControl1.Location = new System.Drawing.Point(0, 0);
            this.tileControl1.Name = "tileControl1";
            this.tileControl1.Size = new System.Drawing.Size(266, 325);
            this.tileControl1.TabIndex = 0;
            this.tileControl1.TilesetsConfigAutoLoad = true;
            this.tileControl1.TilesetsConfigFile = "TilesetsList.config";
            // 
            // TilesetToolForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(266, 325);
            this.Controls.Add(this.tileControl1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Name = "TilesetToolForm";
            this.TabText = "Tileset Manager";
            this.Text = "Tileset Manager";
            this.TopMost = true;
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.TilesetToolForm_FormClosing);
            this.ResumeLayout(false);

        }

        #endregion

        private TileControl tileControl1;




    }
}