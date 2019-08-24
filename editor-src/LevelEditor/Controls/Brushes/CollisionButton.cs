using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using EditorObjects;

namespace Javy.Controls
{
    public partial class CollisionButton : UserControl
    {
        public Collision Collision
        {
            get
            {
                Collision c = new Collision();
                c.Top = checkBox1.Checked;
                c.Right = checkBox2.Checked;
                c.Bottom = checkBox3.Checked;
                c.Left = checkBox4.Checked;
                return c;
            }
            set
            {
                checkBox1.Checked = value.Top;
                checkBox2.Checked = value.Right;
                checkBox3.Checked = value.Bottom;
                checkBox4.Checked = value.Left;
            }
        }

        public bool CollisionTop
        {
            get { return checkBox1.Checked; }
            set { checkBox1.Checked = value; }
        }
        public bool CollisionRight
        {
            get { return checkBox2.Checked; }
            set { checkBox2.Checked = value; }
        }
        public bool CollisionBottom
        {
            get { return checkBox3.Checked; }
            set { checkBox3.Checked = value; }
        }
        public bool CollisionLeft
        {
            get { return checkBox4.Checked; }
            set { checkBox4.Checked = value; }
        }

        
        public CollisionButton()
        {
            InitializeComponent();
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox1.Checked)
            {
                checkBox1.BackgroundImage = Properties.Resources.upP;
            }
            else
            {
                checkBox1.BackgroundImage = Properties.Resources.up;
            }
            checkBoxN_CheckedChanged(sender, e);
        }

        private void checkBox5_CheckedChanged(object sender, EventArgs e)
        {
            checkBox1.Checked = checkBox5.Checked;
            checkBox2.Checked = checkBox5.Checked;
            checkBox3.Checked = checkBox5.Checked;
            checkBox4.Checked = checkBox5.Checked;

            checkBox5.BackgroundImage = checkBox5.Checked ? Properties.Resources.allP 
                                                          : Properties.Resources.all;

        }

        private void checkBoxN_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox1.Checked &&
                checkBox2.Checked &&
                checkBox3.Checked &&
                checkBox4.Checked)
            {
                checkBox5.Checked = true;
            }
            //else checkBox5.Checked = false;

            if (!checkBox1.Checked &&
                !checkBox2.Checked &&
                !checkBox3.Checked &&
                !checkBox4.Checked)
            {
                checkBox5.Checked = false;
            }
           
        }

        private void checkBox4_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox4.Checked)
            {
                checkBox4.BackgroundImage = Properties.Resources.leftP;
            }
            else
            {
                checkBox4.BackgroundImage = Properties.Resources.left;
            }
            checkBoxN_CheckedChanged(sender, e);
        }

        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox2.Checked)
            {
                checkBox2.BackgroundImage = Properties.Resources.rightP;
            }
            else
            {
                checkBox2.BackgroundImage = Properties.Resources.right;
            }
            checkBoxN_CheckedChanged(sender, e);
        }

        private void checkBox3_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox3.Checked)
            {
                checkBox3.BackgroundImage = Properties.Resources.downP;
            }
            else
            {
                checkBox3.BackgroundImage = Properties.Resources.down;
            }
            checkBoxN_CheckedChanged(sender, e);
        }
    }
}
